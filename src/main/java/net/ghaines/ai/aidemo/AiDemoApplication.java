package net.ghaines.ai.aidemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class AiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiDemoApplication.class, args);
	}

}

@RestController
class AiController {
	private final AiService aiService;

	public AiController(AiService aiService) {
		this.aiService = aiService;
	}

	@GetMapping
	ResponseEntity<String> performAI(@RequestParam(value = "q") String q,
									 @RequestParam(value = "acct", required = false) String acct) {
		return ResponseEntity.ok(aiService.performAI(q));
	}

}

@Service
class AiService {
	private final AiClient aiClient;

	private final static Logger LOGGER = LoggerFactory.getLogger(AiService.class);

	public AiService(AiClient aiClient) {
		this.aiClient = aiClient;
	}

	String performAI(String request) {

		var promptQ = """
				Given the below, which most closely matches the request? If none of the
				options are similar, respond "Sorry, I cannot help with that."
								
				Update an address
				Update a phone number
				Transfer client to EJC
				Enter a note
								
				Request: {request}
					""";
		var pt = new PromptTemplate(promptQ);
		var prompt = pt.create(Map.of("request", request));
		var answer = aiClient.generate(prompt).getGeneration().getText();
		LOGGER.info("answer is: {}", answer);

		var page = switch(answer.replace(".", "")) {
			case "Update an address", "Update a phone number" -> "/profile";
			case "Transfer client to EJC" -> "/transfer";
			case "Enter a note" -> "/note";
			default -> "";
		};
		LOGGER.info("routing to page: {}", page);
		return page;
	}
}

@Controller
@RequestMapping
class AiWeb {

	@Value("${speechKey}")
	private String speechKey;

	@GetMapping("/sf")
	String getDemo() {
		return "sf";
	}
	@GetMapping("/acct/{acctId}")
	String getAccount(@PathVariable("acctId") String acctId, Model model) {
		model.addAttribute(acctId);
		model.addAttribute("speechKey", speechKey);
		return "account";
	}

	@GetMapping("/profile")
	String getProfile() {
		return "profile";
	}
	@GetMapping("/transfer")
	String getTransfer() {
		return "transfer";
	}
	@GetMapping("/note")
	String getNote() {
		return "note";
	}
}
