package dev.arunsamra.fileprocessor.config;

import dev.arunsamra.fileprocessor.metadata.MetadataClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "metadata", types = MetadataClient.class)
public class MetadataClientConfig {
}
