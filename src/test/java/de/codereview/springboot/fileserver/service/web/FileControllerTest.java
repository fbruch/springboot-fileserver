package de.codereview.springboot.fileserver.service.web;

import de.codereview.fileserver.api.v1.ConverterResult;
import de.codereview.springboot.fileserver.service.FileResult;
import de.codereview.springboot.fileserver.service.FileService;
import de.codereview.springboot.fileserver.service.plugin.ConverterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc                   // full spring application context...
//@WebMvcTest(FileController.class)     // just the web layer...
@ActiveProfiles("dev")
public class FileControllerTest
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileService fileService;

	@Mock
	private Stream<Path> pathStream;

	@MockBean
	private ConverterService converterService;

	private Map<String, Path> boxes = new HashMap<>();

	private static final String BOX_NAME = "some-random-string";
	private static final Path BOX_PATH = Paths.get("some/random/path");
	private static final String DIR_PATH = "some/sub/dir";
	private static final String FILE_NAME = "dummy-file.md";

	@Before
	public void setUp() {
		boxes = new HashMap<>();
		boxes.put(BOX_NAME, BOX_PATH);
	}

    private String getUrlTemplate()
    {
        return "/fs/" + BOX_NAME + "/" + DIR_PATH + "/" + FILE_NAME;
    }

	@Test
	public void shouldServeFileContentUsingFileService() throws Exception {
        FileResult fileResult = mockFileService();

		MvcResult result = mockMvc.perform(get(getUrlTemplate()).characterEncoding("iso-8859-1"))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		org.junit.Assert.assertThat(result.getResponse().getContentAsByteArray(),
				org.hamcrest.Matchers.equalTo(fileResult.getContent()));
		org.junit.Assert.assertThat(result.getResponse().getHeader("Content-Type"),
				org.hamcrest.Matchers.equalTo("text/markdown;charset=iso8859-1"));
	}

	@Test
	public void shouldUseMimeTypeAndEncodingFromAcceptHeaderWhenRunningConverter() throws Exception {
        FileResult fileResult = mockFileService();

        final Charset UTF8 = Charset.forName("utf-8");
        String CHARSET = UTF8.name();
        String TYPE = MediaType.TEXT_HTML_VALUE;

        when(converterService.isConversionAvailable("text/markdown", "text/html")).thenReturn(true);
        when(converterService.convert(
            any(),eq("text/markdown"), eq("text/html"), eq("dummy-file.md"), eq("iso8859-1"), anyString()))
            .thenReturn(new ConverterResult("dummy-html".getBytes(UTF8),"dummy-title", "UTF-8"));

        MediaType accept = new MediaType(MediaType.TEXT_HTML, UTF8);
//        MvcResult result = mockMvc.perform(get(getUrlTemplate()).accept(TYPE + ";charset=" + CHARSET))
        MvcResult result = mockMvc.perform(get(getUrlTemplate()).accept(accept))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		org.junit.Assert.assertThat(result.getResponse().getHeader("Content-Type"),
				org.hamcrest.Matchers.equalTo(TYPE + ";charset=" + CHARSET));
        org.junit.Assert.assertThat(result.getResponse().getContentAsByteArray(),
            org.hamcrest.Matchers.equalTo("dummy-html".getBytes(UTF8))); // ensure converter is run
	}

    private FileResult mockFileService() throws IOException
    {
        when(fileService.getBoxList()).thenReturn(boxes.keySet());
        when(fileService.getBoxPath(BOX_NAME)).thenReturn(BOX_PATH);
        when(fileService.getBoxPath(BOX_NAME)).thenReturn(BOX_PATH);
//        when(fileService.getBoxEncoding(BOX_NAME)).thenReturn(Charset.defaultCharset().name());
        FileResult result = new FileResult(BOX_NAME, DIR_PATH, FILE_NAME, false);
        result.setEncoding("iso8859-1");
        result.getHeader().put(HttpHeaders.CONTENT_TYPE, "text/markdown");
        result.setTextual(true);
        result.setContent("# markdown".getBytes(Charset.forName("iso8859-1")));
        when(fileService.getFile(anyString(), anyString(), anyBoolean())).thenReturn(result);
//        when(fileService.getFileList(BOX_PATH)).thenReturn(pathStream);
        return result;
    }

    @Test
    public void testHead () throws Exception {
        mockFileService();

        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.head(getUrlTemplate());

        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testOptions () throws Exception {
        ResultMatcher accessHeader = MockMvcResultMatchers.header()
            .string("Allow", "GET,HEAD");

        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.options(getUrlTemplate());

        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(accessHeader)
            .andDo(MockMvcResultHandlers.print());
    }
}
