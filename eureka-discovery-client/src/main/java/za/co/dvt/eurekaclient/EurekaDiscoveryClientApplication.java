package za.co.dvt.eurekaclient;

import com.ctc.wstx.shaded.msv_core.util.Uri;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
@RestController
public class EurekaDiscoveryClientApplication {

	private DiscoveryClient discoveryClient;
	private List<ServiceInstance> instances;
	private RestTemplateBuilder restTemplateBuilder;
	private int next;

	@Autowired
	public EurekaDiscoveryClientApplication(DiscoveryClient discoveryClient, RestTemplateBuilder restTemplateBuilder) {
		this.discoveryClient = discoveryClient;
		this.restTemplateBuilder = restTemplateBuilder;
		next = 0;
	}

	public static void main(String[] args) {
		SpringApplication.run(EurekaDiscoveryClientApplication.class, args);
	}

	@GetMapping("/")
	public String callService(){
		RestTemplate restTemplate = restTemplateBuilder.build();
		instances = discoveryClient.getInstances("service");
		URI baseUrl = instances.get(next).getUri();

		next++;
		if(next == instances.size()){
			next = 0;
		}

		ResponseEntity<String> responseEntity =
				restTemplate.exchange(baseUrl, HttpMethod.GET, null, String.class);

		return responseEntity.getBody();
	}
}
