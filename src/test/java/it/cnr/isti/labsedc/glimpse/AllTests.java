package it.cnr.isti.labsedc.glimpse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import it.cnr.isti.labsedc.glimpse.test.*;
import it.cnr.isti.labsedc.glimpse.test.BPMN.ModelLoaderTest;
import it.cnr.isti.labsedc.glimpse.test.impl.BPMNPathExplorerImplTest;
import it.cnr.isti.labsedc.glimpse.test.impl.ComplexEventProcessorImplTest;
import it.cnr.isti.labsedc.glimpse.test.impl.EventBufferImplTest;
import it.cnr.isti.labsedc.glimpse.test.impl.GlimpseBaseEventImplTest;
import it.cnr.isti.labsedc.glimpse.test.impl.LearnerAssessmentManagerImplTest;
import it.cnr.isti.labsedc.glimpse.test.impl.PathExplorerImplTest;
import it.cnr.isti.labsedc.glimpse.test.impl.RoomManagerImplTest;
import it.cnr.isti.labsedc.glimpse.test.impl.RuleTemplateManagerTest;
import it.cnr.isti.labsedc.glimpse.test.impl.RulesPerPathGeneratorImplTest;
import it.cnr.isti.labsedc.glimpse.test.manager.GlimpseManagerTest;
import it.cnr.isti.labsedc.glimpse.test.manager.ResponseDispatcherTest;
import it.cnr.isti.labsedc.glimpse.test.rules.DroolsRulesManagerTest;
import it.cnr.isti.labsedc.glimpse.test.rules.RuleElementsTest;
import it.cnr.isti.labsedc.glimpse.test.smartbuilding.RoomManagerTest;
import it.cnr.isti.labsedc.glimpse.test.smartbuilding.SmartCampusUserTest;
import it.cnr.isti.labsedc.glimpse.test.utils.ComputeLearnerScoreTest;
import it.cnr.isti.labsedc.glimpse.test.utils.DebugMessagesTest;
import it.cnr.isti.labsedc.glimpse.test.utils.JsonLoggerTest;
import it.cnr.isti.labsedc.glimpse.test.utils.MailNotificationTest;
import it.cnr.isti.labsedc.glimpse.test.utils.ManagerTest;
import it.cnr.isti.labsedc.glimpse.test.utils.NotifierUtilsTest;
import it.cnr.isti.labsedc.glimpse.test.utils.UpdateRoomTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	MainMonitoringTest.class, 
	ModelLoaderTest.class, 
	BPMNPathExplorerImplTest.class, 
	ComplexEventProcessorImplTest.class,
	EventBufferImplTest.class,
	GlimpseBaseEventImplTest.class,
	LearnerAssessmentManagerImplTest.class,
	PathExplorerImplTest.class,
	RoomManagerImplTest.class,
	RulesPerPathGeneratorImplTest.class,
	RuleTemplateManagerTest.class,
	GlimpseManagerTest.class,
	ResponseDispatcherTest.class,
	DroolsRulesManagerTest.class,
	RuleElementsTest.class,
	RoomManagerTest.class,
	SmartCampusUserTest.class,
	ComputeLearnerScoreTest.class,
	DebugMessagesTest.class,
	JsonLoggerTest.class,
	MailNotificationTest.class,
	ManagerTest.class,
	NotifierUtilsTest.class,
	UpdateRoomTest.class
	})

public class AllTests {
}
