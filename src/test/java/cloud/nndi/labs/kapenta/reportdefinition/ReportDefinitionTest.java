package cloud.nndi.labs.kapenta.reportdefinition;

import cloud.nndi.labs.kapenta.parameters.ParameterDefinition;
import com.google.common.collect.Sets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cloud.nndi.labs.kapenta.parameters.ParameterDefinition.optionalParameter;
import static cloud.nndi.labs.kapenta.parameters.ParameterDefinition.requiredParameter;
import static org.junit.Assert.*;

/**
 * ReportDefinitionTest
 */
public class ReportDefinitionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetParameterType() {
        List<ParameterDefinition> params = new ArrayList<>();
        params.add(optionalParameter("subreport_1", Boolean.class));
        ReportDefinition reportDefinition = new ReportDefinition("report",
                "./src/test/resources/test_report.prpt",
                params);

        assertEquals(Boolean.class, reportDefinition.parameterType("subreport_1"));
    }

    @Test
    public void testFailToGetParameterType() {
        List<ParameterDefinition> params = new ArrayList<>();
        params.add(optionalParameter("subreport_1", Boolean.class));
        ReportDefinition reportDefinition = new ReportDefinition("report",
                "./src/test/resources/test_report.prpt",
                params);

        assertEquals(null, reportDefinition.parameterType("non_existent_parameter"));
    }

    @Test
    public void testGetParameters() {
        List<ParameterDefinition> params = new ArrayList<>();

        params.add(optionalParameter("subreport_1", Boolean.class));
        params.add(optionalParameter("subreport_2", Boolean.class));
        params.add(requiredParameter("report_id", String.class));

        ReportDefinition reportDefinition = new ReportDefinition("report",
                "./src/test/resources/test_report.prpt",
                params);

        assertEquals(3, reportDefinition.getParameters().size());
    }

    @Test
    public void testThrowIllegalArgOnEmptyName() {
        ReportDefinition reportDefinition = new ReportDefinition("",
                "./src/test/resources/test_report.prpt",
                Collections.emptyList());

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ReportDefinion must have a valid name");
        reportDefinition.validate();
    }

    @Test
    public void testHasRequiredParametersInSingleSet() {
        List<ParameterDefinition> params = new ArrayList<>();
        params.add(requiredParameter("report_id", Boolean.class));
        ReportDefinition reportDefinition = new ReportDefinition("report",
                "./src/test/resources/test_report.prpt",
                params);

        assertTrue(reportDefinition.hasRequiredParameterNamesIn(Sets.newHashSet("report_id")));
        assertFalse(reportDefinition.hasRequiredParameterNamesIn(
            Sets.newHashSet("subreport_1", "subreport_2")));
    }

    @Test
    public void testHasRequiredParametersInSet() {
        List<ParameterDefinition> params = new ArrayList<>();
        params.add(requiredParameter("report_id", Boolean.class));
        params.add(optionalParameter("subreport_1", Boolean.class));
        params.add(optionalParameter("subreport_2", Boolean.class));
        ReportDefinition reportDefinition = new ReportDefinition("report",
                "./src/test/resources/test_report.prpt",
                params);

        assertTrue(reportDefinition.hasRequiredParameterNamesIn(Sets.newHashSet("report_id", "subreport_1", "subreport_2")));
        assertFalse(reportDefinition.hasRequiredParameterNamesIn(Sets.newHashSet("subreport_1", "subreport_2")));
    }

    @Test
    public void testExtractRequiredParametersFromSet() {
        List<ParameterDefinition> params = new ArrayList<>();
        params.add(requiredParameter("required_param_1", Boolean.class));
        params.add(requiredParameter("required_param_2", Boolean.class));
        params.add(optionalParameter("subreport_1", Boolean.class));
        params.add(optionalParameter("subreport_2", Boolean.class));
        ReportDefinition reportDefinition = new ReportDefinition("report",
                "./src/test/resources/test_report.prpt",
                params);

        // Set with only one required parameter
        assertEquals("required_param_1",
            reportDefinition.extractMissingRequiredParams(
                Sets.newHashSet("required_param_2")));

        // Set with only one required parameter
        assertEquals("required_param_2",
            reportDefinition.extractMissingRequiredParams(
                Sets.newHashSet("required_param_1")));

        // Set with only optional parameters
        assertEquals("required_param_1,required_param_2",
            reportDefinition.extractMissingRequiredParams(
                Sets.newHashSet("subreport_1", "subreport_2")));

        // Set without parameters
        assertEquals("required_param_1,required_param_2",
            reportDefinition.extractMissingRequiredParams(Collections.emptySet()));
    }
}
