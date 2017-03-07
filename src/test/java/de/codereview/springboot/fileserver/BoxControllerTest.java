package de.codereview.springboot.fileserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc // full spring application context...
@WebMvcTest             // just the web layer...
public class BoxControllerTest
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HtmlService htmlService;

	@MockBean
	private FileService fileService;

	@Mock
	private Stream<Path> pathStream1;

	@Mock
	private Stream<Path> pathStream2;

	@Mock
	private Path boxPath1;

	@Mock
	private Path boxPath2;

	@Test
	public void boxListShouldContainHeadingForEachBox() throws Exception {
		Map<String, Path> boxes = new HashMap<>();
		boxes.put("box1", boxPath1);
		boxes.put("box2", boxPath2);

		when(fileService.getBoxList()).thenReturn(boxes.keySet());
		when(fileService.getBoxPath("box1")).thenReturn(boxPath1);
		when(fileService.getBoxPath("box2")).thenReturn(boxPath2);

		when(fileService.listDir(boxPath1)).thenReturn(pathStream1);
		when(fileService.listDir(boxPath2)).thenReturn(pathStream2);
		// TODO: let boxPath1 and 2 return specific path names
		// TODO: let pathStream1 and 2 return specific root files/dirs

		MvcResult result = mockMvc.perform(get("/file")).andDo(print()).andExpect(status().isOk()).andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		org.junit.Assert.assertThat(contentAsString,
				org.hamcrest.Matchers.containsString("<h2>box1</h2>"));
		org.junit.Assert.assertThat(contentAsString,
				org.hamcrest.Matchers.containsString("<h2>box2</h2>"));
		// TODO: check for specific mocked path names of boxPath1 and 2
//		org.junit.Assert.assertThat(contentAsString,
//				org.hamcrest.Matchers.containsString(boxPath1.toString()));
//		org.junit.Assert.assertThat(contentAsString,
//				org.hamcrest.Matchers.containsString(boxPath2.toString()));
		// TODO: check for specific root files/dirs from pathStream1 and 2
	}
}
