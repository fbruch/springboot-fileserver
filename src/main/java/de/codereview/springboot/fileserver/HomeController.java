package de.codereview.springboot.fileserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class HomeController
{
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private List<Endpoint> endpoints;

    @Autowired
    public HomeController(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
	public Object index()
	{
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title>");
        builder.append("springboot-fileserver");
        builder.append("</title></head><body><h2>");
        builder.append("springboot-fileserver");
        builder.append("</h2><ul>");
        builder.append("<li><a href=\"fb\">fileservice-browser</a></li>");
        builder.append("</ul><h2>");
        builder.append("Spring Boot Actuator");
        builder.append("</h2><ul>");
        builder.append("<li><a href=\"/act/actuator/\">actuator</a></li>");
        endpoints.forEach(endpoint ->
            builder.append("<li><a href=\"/act/" + endpoint.getId()
                + "\">" + endpoint.getId() + "</a></li>"));
        builder.append("</ul></body></html>");
        return builder.toString();
	}
}
