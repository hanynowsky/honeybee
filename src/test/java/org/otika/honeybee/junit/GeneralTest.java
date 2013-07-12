package org.otika.honeybee.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;
import org.otika.honeybee.model.Help;
import org.otika.honeybee.view.HelpBean;

@RunWith(Arquillian.class)
public class GeneralTest {

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	EntityManager em;
	@Inject
	HelpBean helpBean;

	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap
				.create(WebArchive.class, "testhoneybee.war")
				.addClasses(Help.class, HelpBean.class)
				.addAsResource("META-INF/test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		// Deploy our test datasource
		// .addAsWebInfResource("test-ds.xml");
	}

	@Test
	public void testJUnitIsWorking() {
		String string = "Junit is working fine";
		assertEquals("Junit is working fine", string);
	}

	@Test
	public void testBool() {
		boolean bool = true;
		assertTrue(bool);
	}

	@Test
	public void testHelpInsertion() {
		Help help = new Help();
		help.setContent("some content");
		help.setTitle("Test Help");
		help.setValid(false);
		help.setVersion(0);
		em.persist(help);
		assertNotNull(help.getId());
		Logger.getLogger(getClass().getName()).info("Help creation successful");
	}

} // END OF CLASS
