package de.codereview.springboot.fileserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController
{
	private static final Logger log = LoggerFactory.getLogger(FileController.class);

	final FileService fileService;
	final HtmlService htmlService;

	private static final int PREFIX_PATH_LENGTH = "/file/".length();

	@Autowired
	public FileController(FileService fileService, HtmlService htmlService)
	{
		this.fileService = fileService;
		this.htmlService = htmlService;
	}

	@RequestMapping(value = "/{box}/**", method = RequestMethod.GET)
	public Object file(@PathVariable String box,
					   HttpServletRequest request,
					   @RequestHeader Map<String, String> header)
	{
		System.out.println("header = " + header);
		String path = (String) request.getAttribute(
				HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		path = path.substring(PREFIX_PATH_LENGTH + box.length() + 1);
		Path filePath = fileService.getFilePath(box, path);
		try
		{
			if (Files.isDirectory(filePath))
			{
				Path boxPath = fileService.getBoxPath(box);
				return htmlService.listDireectory(filePath, box, boxPath, request.getContextPath());
			} else
			{
				return fileService.readFile(filePath);
			}
		} catch (IOException e)
		{
			String msg = "Error accesing box storage";
			log.error(msg);
			throw new IllegalArgumentException(msg, e);
		}
	}
}
