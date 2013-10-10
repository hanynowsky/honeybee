package org.otika.honeybee.util;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.servlet.config.DispatchType;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Header;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Method;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.RequestParameter;
import org.ocpsoft.rewrite.servlet.config.SendStatus;
import org.ocpsoft.rewrite.servlet.config.URL;

/**
 * 
 * @author Hanine HAMZIOUI ALMADANI <hanynowsky@gmail.com>
 * 
 */
public class RewriteSecurityFirewall extends HttpConfigurationProvider {

	// TODO not yet activated
	// To activate : declare a file in META-INF/services/org.ocpsoft.Rewrite.config.ConfigurationProvider
	
	@Override
	public Configuration getConfiguration(ServletContext arg0) {
		/*
		 * Allowed URL characters
		 */
		Constraint<String> selectedCharacters = new Constraint<String>() {
			@Override
			public boolean isSatisfiedBy(Rewrite event,
					EvaluationContext context, String value) {
				return value.matches("[a-zA-Z0-9/:&?.-=_+]*");
			}
		};

		/*
		 * Block any in-bound request not forwarded from within the application
		 * itself.
		 */
		return ConfigurationBuilder
				.begin()
				.addRule()
				.when(Direction
						.isInbound()
						.andNot(DispatchType.isForward())
						.and(RequestParameter.matchesAll("{name}", "{value}")
								.orNot(RequestParameter.exists("{}")))
						.and(Path.captureIn("path")).and(URL.captureIn("url")))
				.perform(Forward.to("{path}"))
				.where("url")
				.constrainedBy(selectedCharacters)
				.where("name")
				.constrainedBy(selectedCharacters)
				.where("value")
				.constrainedBy(selectedCharacters)
				.addRule()
				.when(Direction.isInbound().andNot(DispatchType.isForward()))
				.perform(SendStatus.error(404))
				.addRule()
				.when(Direction.isInbound().and(Method.isPost())
						.andNot(DispatchType.isForward())
						.and(Path.captureIn("path"))
						.and(Header.matches("{name}", "{value}"))
						.orNot(Header.exists("{}")))
				.perform(Forward.to("{path}")).where("name")
				.constrainedBy(selectedCharacters).where("value")
				.constrainedBy(selectedCharacters);
	}

	@Override
	public int priority() {

		return -1000;
	}

	// TODO use Rewrite security firewall technique
}
