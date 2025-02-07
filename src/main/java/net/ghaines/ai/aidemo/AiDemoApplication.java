package net.ghaines.ai.aidemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

	@Bean
	ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build();
	}

}

@RestController
class AiController {
	private final AiService aiService;

	public AiController(AiService aiService) {
		this.aiService = aiService;
	}

	@GetMapping
	ResponseEntity<String> performAI(@RequestParam(value = "q") String q) {
		return ResponseEntity.ok(aiService.performAI(q));
	}

}

@Service
class AiService {
	private final ChatClient chatClient;

	private final static Logger LOGGER = LoggerFactory.getLogger(AiService.class);

	public AiService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	String performAI(String request) {

		var promptQ = """
				Given the below, which most closely matches the request? If none of the
				options are similar, respond "Sorry, I cannot help with that." Do not respond with "Option:", 
				just provide the exact answer as listed below.
								
				Update an address
				Update a phone number
				Transfer client to EJC
				Enter a note
				Home page
								
				Request: {request}
					""";
		var pt = new PromptTemplate(promptQ);
		var prompt = pt.create(Map.of("request", request));
		var answer = chatClient.prompt(prompt).call().content();
		LOGGER.info("answer is: {}", answer);

		var page = switch (answer.replace(".", "")) {
			case "Update an address",
					"Update a phone number",
					"Update contact",
					"Update profile"
						-> "/contacts";
			case "Transfer client to EJC" -> "/transfers";
			case "Enter a note" -> "/notes";
			case "Home page" -> "/home";
			default -> "";
		};
		LOGGER.info("routing to page: {}", page);
		return page;
	}
}

@Controller
@RequestMapping
class AiWeb {

	@Value("${speech.key}")
	private String speechKey;

	@GetMapping("/home")
	String getHome(Model model) {
		model.addAttribute("speechKey", speechKey);
		return "home";
	}

	@GetMapping("/acct/{acctId}")
	String getAccount(@PathVariable("acctId") String acctId, Model model) {
		model.addAttribute(acctId);
		model.addAttribute("speechKey", speechKey);
		return "account";
	}

	@GetMapping("/contacts")
	String getContacts(Model model) {
		model.addAttribute("speechKey", speechKey);
		return "contacts";
	}

	@GetMapping("/profile")
	String getProfile(Model model) {
		model.addAttribute("speechKey", speechKey);
		return "contacts";
	}

	@GetMapping("/transfer")
	String getTransfer() {
		return "transfer";
	}

	@GetMapping("/note")
	String getNote(Model model) {
		model.addAttribute("speechKey", speechKey);
		return "note";
	}

	@GetMapping("/notes")
	String s(Model model) {
		model.addAttribute("speechKey", speechKey);
		return "notes";
	}

	@GetMapping("/transfers")
	String transfers(Model model) {
		model.addAttribute("speechKey", speechKey);
		return "transfers";
	}
}
