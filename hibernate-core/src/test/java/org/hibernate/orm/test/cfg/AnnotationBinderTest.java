/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.cfg;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.internal.EntityBinder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.internal.CoreMessageLogger;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.junit.Rule;
import org.junit.Test;

import org.jboss.logging.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;

import static org.junit.Assert.assertTrue;

/**
 * @author Dominique Toupin
 */
@TestForIssue(jiraKey = "HHH-10456")
public class AnnotationBinderTest {

	@Rule
	public LoggerInspectionRule logInspection = new LoggerInspectionRule(
			Logger.getMessageLogger( CoreMessageLogger.class, EntityBinder.class.getName() ) );

	@Test
	public void testInvalidPrimaryKeyJoinColumnAnnotationMessageContainsClassName() throws Exception {

		Triggerable triggerable = logInspection.watchForLogMessages( "HHH000137" );

		try (StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().build()) {

			Metadata metadata = new MetadataSources( serviceRegistry )
					.addAnnotatedClass( InvalidPrimaryKeyJoinColumnAnnotationEntity.class )
					.buildMetadata();

			assertTrue( "Expected warning HHH00137 but it wasn't triggered", triggerable.wasTriggered() );
			assertTrue(
					"Expected invalid class name in warning HHH00137 message but it does not appear to be present; got " + triggerable.triggerMessage(),
					triggerable.triggerMessage()
							.matches( ".*\\b\\Q" + InvalidPrimaryKeyJoinColumnAnnotationEntity.class.getName() + "\\E\\b.*" )
			);
		}
	}

	@Entity
	@PrimaryKeyJoinColumn
	public static class InvalidPrimaryKeyJoinColumnAnnotationEntity {

		private String id;

		@Id
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

}
