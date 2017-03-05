package de.codereview.springboot.fileserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/")
public class BoxController
{
	private static final Logger log = LoggerFactory.getLogger(BoxController.class);

	final FileService fileService;
	final HtmlService htmlService;

	@Autowired
	public BoxController(FileService fileService, HtmlService htmlService)
	{
		this.fileService = fileService;
		this.htmlService = htmlService;
	}

	@RequestMapping(value = "/file", method = RequestMethod.GET)
	public Object listBoxesWithRootFiles(HttpServletRequest request)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>");
		builder.append("Boxes");
		builder.append("</title></head>");
		builder.append("<h1>");
		builder.append("Box list");
		builder.append("</h1><body>");
		for (String box : fileService.getBoxList())
		{
			builder.append("<h2>");
			builder.append(box);
			builder.append("</h2>");
			try
			{
				Path boxPath = fileService.getBoxPath(box);
				htmlService.listDirectoryContentAsLinks(
						builder, box, boxPath, boxPath, request.getContextPath());
			}
			catch (IOException e)
			{
				String msg = "Error parsing box directory";
				log.error(msg, e);
				throw new RuntimeException(msg, e);
			}
		}
		builder.append("</body></html>");
		return builder.toString();
	}
}
