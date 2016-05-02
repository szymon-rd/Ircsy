package pl.jaca.ircsy.webapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Jaca777
 *         Created 2016-05-02 at 16
 */

@Configuration
@ComponentScan(basePackages = "pl.jaca.ircsy.webapp",excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)})
public class RootConfig {
}
