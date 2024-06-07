package december.spring.studywithme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import jakarta.annotation.PostConstruct;

@Configuration
public class RedisConfig {
	@Value("${spring.data.redis.mail.host}")
	private String host;
	
	@Value("${spring.data.redis.mail.port}")
	private int port;
	
	private LettuceConnectionFactory lettuceConnectionFactory;
	
	@PostConstruct
	public void init() {
		lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
		lettuceConnectionFactory.start();
	}
	
	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory);
		return redisTemplate;
	}
}
