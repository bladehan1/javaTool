package third.elastic;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Arrays;
import java.util.Objects;

import static third.elastic.ElasticsearchProperties.ADDRESS_LENGTH;


public class ElasticsearchAutoConfiguration {
	private final ElasticsearchProperties properties;
	//加载配置
	public ElasticsearchAutoConfiguration(ElasticsearchProperties properties) {
		this.properties = properties;
	}
	//创建builder
	public RestClientBuilder restClientBuilder() {
		HttpHost[] hosts = Arrays.stream(properties.getAddress())
				.map(this::makeHttpHost)
				.filter(Objects::nonNull)
				.toArray(HttpHost[]::new);
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		Credentials credentials = new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword());
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		return RestClient.builder(hosts)
				.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.disableAuthCaching()
						.setDefaultCredentialsProvider(credentialsProvider));
	}
	//创建Client
	public RestHighLevelClient highLevelClient(RestClientBuilder restClientBuilder) {
		return new RestHighLevelClient(restClientBuilder);
	}

	private HttpHost makeHttpHost(String address) {
		assert StringUtils.isNotEmpty(address);
		String[] addressArray = address.split(":");
		if (addressArray.length == ADDRESS_LENGTH) {
			String ip = addressArray[0];
			int port = Integer.parseInt(addressArray[1]);
			return new HttpHost(ip, port, properties.getScheme());
		} else {
			return null;
		}
	}

	/**
	 *   index: pepper-user-test-aliase
	 *   reindex: pepper-user-test-v5
	 *   groupIndex: pepper_group
	 *   liveIndex: pepper_live
	 *   scheme: http
	 *   address: 10.208.45.178:9200,10.208.45.160:9200,10.208.45.179:9200,10.208.49.141:9200,10.208.49.159:9200,10.208.49.177:9200
	 *   username: admin
	 *   password: admin
	 *   task:
	 * @return
	 */
	public static  RestHighLevelClient caseTest(){
		ElasticsearchProperties properties=new ElasticsearchProperties();
		String[] addres=new String[]{"10.208.45.178:9200","10.208.49.177:9200"};
		properties.setAddress(addres);
		properties.setScheme("http");
		properties.setUsername("admin");
		properties.setPassword("admin");
		ElasticsearchAutoConfiguration elasticsearchAutoConfiguration=new ElasticsearchAutoConfiguration(properties);
		RestClientBuilder restClientBuilder= elasticsearchAutoConfiguration.restClientBuilder();
		return elasticsearchAutoConfiguration.highLevelClient(restClientBuilder);
	}
}
