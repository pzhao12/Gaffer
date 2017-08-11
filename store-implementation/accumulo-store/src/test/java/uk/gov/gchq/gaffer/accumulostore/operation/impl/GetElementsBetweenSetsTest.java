package uk.gov.gchq.gaffer.accumulostore.operation.impl;

import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloTestData;
import uk.gov.gchq.gaffer.data.element.id.DirectedType;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.SeedMatching.SeedMatchingType;
import uk.gov.gchq.gaffer.operation.graph.SeededGraphFilters;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GetElementsBetweenSetsTest extends OperationTest {
    private static final JSONSerialiser serialiser = new JSONSerialiser();

    @Override
    protected Class<? extends Operation> getOperationClass() {
        return GetElementsBetweenSets.class;
    }

    @Test
    @Override
    public void shouldSerialiseAndDeserialiseOperation() throws SerialisationException {
        // Given
        final GetElementsBetweenSets op = new GetElementsBetweenSets.Builder()
                .input(AccumuloTestData.SEED_SOURCE_1, AccumuloTestData.SEED_DESTINATION_1)
                .inputB(AccumuloTestData.SEED_SOURCE_2, AccumuloTestData.SEED_DESTINATION_2)
                .build();

        // When
        byte[] json = serialiser.serialise(op, true);

        final GetElementsBetweenSets deserialisedOp = serialiser.deserialise(json, GetElementsBetweenSets.class);

        // Then
        final Iterator itrSeedsA = deserialisedOp.getInput().iterator();
        assertEquals(AccumuloTestData.SEED_SOURCE_1, itrSeedsA.next());
        assertEquals(AccumuloTestData.SEED_DESTINATION_1, itrSeedsA.next());
        assertFalse(itrSeedsA.hasNext());

        final Iterator itrSeedsB = deserialisedOp.getInputB().iterator();
        assertEquals(AccumuloTestData.SEED_SOURCE_2, itrSeedsB.next());
        assertEquals(AccumuloTestData.SEED_DESTINATION_2, itrSeedsB.next());
        assertFalse(itrSeedsB.hasNext());

    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final GetElementsBetweenSets getElementsBetweenSets = new GetElementsBetweenSets.Builder()
                .input(AccumuloTestData.SEED_B)
                .inputB(AccumuloTestData.SEED_A)
                .directedType(DirectedType.UNDIRECTED)
                .inOutType(SeededGraphFilters.IncludeIncomingOutgoingType.INCOMING)
                .option(AccumuloTestData.TEST_OPTION_PROPERTY_KEY, "true")
                .view(new View.Builder()
                        .edge("testEdgeGroup")
                        .build())
                .build();
        assertEquals("true", getElementsBetweenSets.getOption(AccumuloTestData.TEST_OPTION_PROPERTY_KEY));
        assertEquals(DirectedType.UNDIRECTED, getElementsBetweenSets.getDirectedType());
        assertEquals(SeededGraphFilters.IncludeIncomingOutgoingType.INCOMING, getElementsBetweenSets.getIncludeIncomingOutGoing());
        assertEquals(AccumuloTestData.SEED_B, getElementsBetweenSets.getInput().iterator().next());
        assertEquals(AccumuloTestData.SEED_A, getElementsBetweenSets.getInputB().iterator().next());
        assertNotNull(getElementsBetweenSets.getView());
    }

    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final View view = new View.Builder()
                .edge("testEdgeGroup")
                .build();
        final GetElementsBetweenSets getElementsBetweenSets = new GetElementsBetweenSets.Builder()
                .input(AccumuloTestData.SEED_B)
                .inputB(AccumuloTestData.SEED_A)
                .directedType(DirectedType.UNDIRECTED)
                .inOutType(SeededGraphFilters.IncludeIncomingOutgoingType.INCOMING)
                .option(AccumuloTestData.TEST_OPTION_PROPERTY_KEY, "true")
                .seedMatching(SeedMatchingType.EQUAL)
                .view(view)
                .build();

        // When
        final GetElementsBetweenSets clone = (GetElementsBetweenSets) getElementsBetweenSets.shallowClone();

        // Then
        assertEquals("true", clone.getOption(AccumuloTestData.TEST_OPTION_PROPERTY_KEY));
        assertEquals(DirectedType.UNDIRECTED, clone.getDirectedType());
        assertEquals(SeedMatchingType.EQUAL, clone.getSeedMatching());
        assertEquals(SeededGraphFilters.IncludeIncomingOutgoingType.INCOMING, clone.getIncludeIncomingOutGoing());
        assertEquals(AccumuloTestData.SEED_B, clone.getInput().iterator().next());
        assertEquals(AccumuloTestData.SEED_A, clone.getInputB().iterator().next());
        assertEquals(view, clone.getView());
    }
}
