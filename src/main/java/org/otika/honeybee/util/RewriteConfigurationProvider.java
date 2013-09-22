package org.otika.honeybee.util;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * Configuration provider for OcpSoft Rewrite
 * 
 * @author Hanine. H. Al Madani <hanynowsky@gmail.com>
 * 
 */

public class RewriteConfigurationProvider extends HttpConfigurationProvider {
	@Override
	public int priority() {
		return 10;
	}

	@Override
	public Configuration getConfiguration(ServletContext arg0) {
		
		// TODO add security filter : ocpsoft : secure page
		return ConfigurationBuilder
				.begin()
				.addRule()
				.when(Direction.isInbound().and(
						Path.matches("/misc/void.xhtml")))
				.perform(Forward.to("/bmi/bmi.xhtml"))
				.addRule(Join.path("/misc/{param}").to("/misc/{param}.xhtml"))
				.addRule(Join.path("/plant/{param}").to("/plant/{param}.xhtml"))
				.addRule(
						Join.path("/language/{param}").to(
								"/language/{param}.xhtml"))
				.addRule(
						Join.path("/enduser/{param}").to(
								"/enduser/{param}.xhtml"))
				.addRule(
						Join.path("/prescription/{param}").to(
								"/prescription/{param}.xhtml"))
				.addRule(
						Join.path("/author/{param}")
								.to("/author/{param}.xhtml"))
				.addRule(
						Join.path("/witness/{param}").to(
								"/witness/{param}.xhtml"))
				.addRule(
						Join.path("/virtue/{param}")
								.to("/virtue/{param}.xhtml"))
				.addRule(
						Join.path("/defect/{param}")
								.to("/defect/{param}.xhtml"))
				.addRule(
						Join.path("/bodypart/{param}").to(
								"/bodypart/{param}.xhtml"))
				.addRule(
						Join.path("/ingredient/{param}").to(
								"/ingredient/{param}.xhtml"))
				.addRule(
						Join.path("/complement/{param}").to(
								"/complement/{param}.xhtml"))
				.addRule(Join.path("/honey/{param}").to("/honey/{param}.xhtml"))
				.addRule(
						Join.path("/substance/{param}").to(
								"/substance/{param}.xhtml"))
				.addRule(Join.path("/store/{param}").to("/store/{param}.xhtml"))
				.addRule(Join.path("/help/{param}").to("/help/{param}.xhtml"))
				.addRule(
						Join.path("/usergroup/{param}").to(
								"/usergroup/{param}.xhtml"))
				.addRule(Join.path("/home").to("/index.xhtml"))
				.addRule(Join.path("/bmi/{param}").to("/bmi/{param}.xhtml"))
				.addRule(Join.path("/signin").to("/signin.xhtml"))
				.addRule(Join.path("/login").to("/login.xhtml"))
				.addRule(Join.path("/logout").to("/logout.xhtml"))
				.addRule(Join.path("/error").to("/error.xhtml"))
				.addRule(Join.path("/loginerror").to("/loginerror.xhtml"))
				.addRule(Join.path("/admin/test").to("/admin/test.xhtml")) ;
	}
}