package se.gsc.stenmark.gscenduro.compmanagement.test;

import static org.junit.Assert.*;
import org.junit.Test;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.ResultList;
import se.gsc.stenmark.gscenduro.compmanagement.Results;
import se.gsc.stenmark.gscenduro.compmanagement.StageResult;


public class CompetitionTest {

	@Test
	public void test() {
		final String COMP_CLASS_TO_TEST = "";
		final int EXPECTED_NUM_COMPETITORS = 4;
		final int EXPECTED_NUM_STAGES = 3;
		
		//Init Competition with test parameters
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72");
		competition.getCompetitors().add("Andreas S", "2079749", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Peter B", "2065396", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Sverker G", "2078056", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Ingemar G", "2079747", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.setCompetitionDate("2015-09-12");
		competition.setCompetitionType( Competition.SVARTVITT_TYPE);
		competition.setCompetitionName("Competition test name");
		
		//Assert some basic competition fields
		assertEquals(EXPECTED_NUM_STAGES, competition.getStages().size() );
		assertEquals( 71, competition.getStages().get(0).getStart() );
		assertEquals( 72, competition.getStages().get(0).getFinish() );
		assertEquals( 71, competition.getStages().get(1).getStart() );
		assertEquals( 72, competition.getStages().get(1).getFinish() );
		assertEquals( 71, competition.getStages().get(2).getStart() );
		assertEquals( 72, competition.getStages().get(2).getFinish() );

		assertEquals(EXPECTED_NUM_COMPETITORS, competition.getCompetitors().getCompetitors().size() );
		assertEquals(1, competition.getCompetitors().getCompetitorClasses().size());
		assertEquals( COMP_CLASS_TO_TEST, competition.getCompetitors().getCompetitorClasses().get(0) );
		
		//Calculate results
		competition.calculateResults();
		
		//No stagetimes are reported yet. Verify that totalTime is set to NO_TIME_FOR_COMPETITION 
		//and that each stage result is set to NO_TIME_FOR_STAGE
		//and that rank is set to RANK_DNF
		ResultList<Results> results = competition.getResults();
		assertNotNull(results);
		Results totalResult = results.getTotalResult(COMP_CLASS_TO_TEST);
		assertNotNull(totalResult);
		System.out.println("Total results is: " + totalResult);
		assertEquals(EXPECTED_NUM_COMPETITORS, totalResult.getTotalTimeResult().size() );
		assertEquals(EXPECTED_NUM_STAGES, results.getAllStageResults(COMP_CLASS_TO_TEST).size() );
		assertEquals(EXPECTED_NUM_STAGES+1, results.getAllResults(COMP_CLASS_TO_TEST).size() );
		for( StageResult currentTotalTimeResult : totalResult.getTotalTimeResult()){
			assertEquals( (Long)Competition.NO_TIME_FOR_COMPETITION, currentTotalTimeResult.getStageTime() );
			assertEquals( (Integer)Competition.RANK_DNF, currentTotalTimeResult.getRank() );
			assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, currentTotalTimeResult.getStageTimesBack() );
		}
		
		for( int stageNumber = 1; stageNumber <= EXPECTED_NUM_STAGES; stageNumber++){
			Results stageResult = results.getStageResult(stageNumber, COMP_CLASS_TO_TEST);
			System.out.println("Stage " + stageNumber + " results: " + stageResult );
			for( StageResult currentStageResult : stageResult.getTotalTimeResult()){
				assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, currentStageResult.getStageTime() );
				assertEquals( (Integer)Competition.RANK_DNF, currentStageResult.getRank() );
				assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, currentStageResult.getStageTimesBack() );
			}
		}

		

		
		
	}

}
