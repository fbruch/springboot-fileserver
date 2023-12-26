package de.codereview.springboot.fileserver.service.web;

import de.codereview.fileserver.api.v1.ConverterResult;
import de.codereview.springboot.fileserver.Application;
import de.codereview.springboot.fileserver.service.FileResult;
import de.codereview.springboot.fileserver.service.FileService;
import de.codereview.springboot.fileserver.service.plugin.ConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc                   // full spring application context...
//@WebMvcTest(FileController.class)     // just the web layer...
@ActiveProfiles("dev")
public class FileControllerTest
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileService fileService;

	@MockBean
	private ConverterService converterService;

	private Map<String, Path> boxes = new HashMap<>();

	private static final String BOX_NAME = "some-random-string";
	private static final Path BOX_PATH = Paths.get("some/random/path");
	private static final String DIR_PATH = "some/sub/dir";
	private static final String FILE_NAME = "dummy-file.md";

	@BeforeEach
	public void setUp() {
		boxes = new HashMap<>();
		boxes.put(BOX_NAME, BOX_PATH);
	}

    private String getUrlTemplate()
    {
        return "/fs/" + BOX_NAME + "/" + DIR_PATH + "/" + FILE_NAME;
    }

    private FileResult mockFileService() throws IOException
    {
        when(fileService.getBoxList()).thenReturn(boxes.keySet());
        when(fileService.getBoxPath(BOX_NAME)).thenReturn(BOX_PATH);
        when(fileService.getBoxPath(BOX_NAME)).thenReturn(BOX_PATH);
        when(fileService.readFile(any())).thenReturn("dummy-content".getBytes(StandardCharsets.UTF_8));
//        when(fileService.getBoxEncoding(BOX_NAME)).thenReturn(Charset.defaultCharset().name());
        FileResult result = new FileResult(BOX_NAME, DIR_PATH, FILE_NAME, false);
        result.setEncoding("iso8859-1");
        result.getHeader().put(HttpHeaders.CONTENT_TYPE, "text/markdown");
        result.setTextual(true);
        result.setLanguage("de");
//        result.setContent("# markdown".getBytes(Charset.forName("iso8859-1")));
        when(fileService.getFile(anyString(), anyString(), anyBoolean())).thenReturn(result);
//        when(fileService.getFileList(BOX_PATH)).thenReturn(pathStream);
        return result;
    }

    @Test
	public void shouldServeFileContentUsingFileService() throws Exception {
        FileResult fileResult = mockFileService();

		MvcResult result = mockMvc.perform(get(getUrlTemplate()).characterEncoding("iso-8859-1"))
				.andDo(print()).andExpect(status().isOk()).andReturn();

//		org.junit.Assert.assertThat(result.getResponse().getContentAsByteArray(),
//				org.hamcrest.Matchers.equalTo(fileResult.getContent()));
		assertEquals("text/markdown;charset=iso8859-1", result.getResponse().getHeader("Content-Type"));
	}

	@Test
	public void shouldUseMimeTypeAndEncodingFromAcceptHeaderWhenRunningConverter() throws Exception {
        FileResult fileResult = mockFileService();

        final Charset UTF8 = StandardCharsets.UTF_8;
        String CHARSET = UTF8.name();
        String TYPE = MediaType.TEXT_HTML_VALUE;

        when(converterService.isConversionAvailable("text/markdown", "text/html")).thenReturn(true);
        when(converterService.convert(
            any(),eq("text/markdown"), eq("text/html"), eq("dummy-file.md"), anyString(), eq("iso8859-1"), anyString()))
            .thenReturn(new ConverterResult("dummy-html".getBytes(UTF8),"dummy-title", "UTF-8"));

        MediaType accept = new MediaType(MediaType.TEXT_HTML, UTF8);
//        MvcResult result = mockMvc.perform(get(getUrlTemplate()).accept(TYPE + ";charset=" + CHARSET))
        MvcResult result = mockMvc.perform(get(getUrlTemplate()).accept(accept))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		assertEquals(TYPE + ";charset=" + CHARSET, result.getResponse().getHeader("Content-Type"));
        assertArrayEquals("dummy-html".getBytes(UTF8), result.getResponse().getContentAsByteArray()); // ensure converter is run
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
            .string("Allow", "GET,HEAD,OPTIONS");

        MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.options(getUrlTemplate());

        mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(accessHeader)
            .andDo(MockMvcResultHandlers.print());
    }
}
