package de.codereview.springboot.fileserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TODO: mock filesystem access, if done in BoxController
 */
@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc // full spring application context...
@WebMvcTest             // just the web layer...
public class FileControllerTest
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HtmlService htmlService;

	@MockBean
	private FileService fileService;

	@Captor
	ArgumentCaptor<String> stringCaptor;

	@Captor
	ArgumentCaptor<byte[]> byteArrayCaptor;

	private Map<String, Path> boxes = new HashMap<>();

	private static final String BOX_NAME = "some-random-string";
	private static final Path BOX_PATH = Paths.get("src/test/resources/box");
	private static final String DIR_PATH = "data";
	private static final String FILE_PATH = "application-json.json";

	@Before
	public void setUp() {
		boxes = new HashMap<>();
		boxes.put(BOX_NAME, BOX_PATH);
	}

	@Test
	public void shouldServeFileContentUsingFileService() throws Exception {
		when(fileService.getBoxList()).thenReturn(boxes.keySet());
		when(fileService.getBoxPath(anyString())).thenReturn(BOX_PATH);
		when(fileService.getFilePath(anyString(), anyString())).thenReturn(
				BOX_PATH.resolve(DIR_PATH + "/" + FILE_PATH));
		when(fileService.listDir(BOX_PATH)).thenReturn(Files.list(BOX_PATH));
		byte[] content = {42, 43, 44};
		when(fileService.readFile(any())).thenReturn(content);

		MvcResult result = mockMvc.perform(get("/file/" + BOX_NAME + "/" + DIR_PATH + "/" + FILE_PATH))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		org.junit.Assert.assertThat(result.getResponse().getContentAsByteArray(),
				org.hamcrest.Matchers.equalTo(content));

		// TODO: make FileServiceTest starting with this...
//		org.junit.Assert.assertThat((long)result.getResponse().getContentAsByteArray().length,
//				org.hamcrest.Matchers.equalTo(Files.size(BOX_PATH.resolve(DIR_PATH + "/" + FILE_PATH))));
	}
}
