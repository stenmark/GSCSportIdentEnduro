package se.gsc.stenmark.gscenduro.compmanagement.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.ResultList;
import se.gsc.stenmark.gscenduro.compmanagement.Results;
import se.gsc.stenmark.gscenduro.compmanagement.StageResult;


public class CompetitionTest {

	@Test
	public void test() {
		final String COMP_CLASS_TO_TEST = "";
		final int EXPECTED_NUM_COMPETITORS = 5;
		final int EXPECTED_NUM_STAGES = 3;
		
		//Init Competition with test parameters
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72");
		competition.getCompetitors().add("Andreas S", "2079749", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Peter B", "2065396", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Sverker G", "2078056", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Ingemar G", "2079747", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Släggan", "2078082", "", COMP_CLASS_TO_TEST, "0", "0", 0);
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
		
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		//Calculate results
		competition.calculateResults();
		
		//No stagetimes are reported yet. Verify that totalTime is set to NO_TIME_FOR_COMPETITION 
		//and that each stage result is set to NO_TIME_FOR_STAGE
		//and that rank is set to RANK_DNF
		ResultList<Results> results = competition.getResults();
		List<Results> landscapeResults = competition.getResultLandscape();
		assertNotNull(results);
		assertNotNull(landscapeResults);
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
		
		assertEquals(EXPECTED_NUM_COMPETITORS, landscapeResults.size());
		for( Results competitorResult : landscapeResults){
			int stageNumber = 0;
			for( StageResult competitorStageResult : competitorResult.getStageResult() ){
				if(stageNumber == 0){
					assertEquals((Long)Competition.NO_TIME_FOR_COMPETITION, competitorStageResult.getStageTime() );
				}
				else{
					assertEquals((Long)Competition.NO_TIME_FOR_STAGE, competitorStageResult.getStageTime() );
				}
				stageNumber++;
				assertEquals((Long)Competition.NO_TIME_FOR_STAGE, competitorStageResult.getStageTimesBack());
				assertEquals((Integer)Competition.RANK_DNF, competitorStageResult.getRank());
			}	
		}
		
		
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		System.out.println("Add results for competitor 1: Andreas S - 2079749");
		Competitor competitorAndreas = competition.getCompetitors().getByCardNumber(2079749);
		assertEquals(2079749, competitorAndreas.getCardNumber() );
		Card cardAndreas = new Card();
		cardAndreas.setCardNumber(2079749);
		cardAndreas.setNumberOfPunches(6);
		List<Punch> andreasPunches = new ArrayList<>();
		andreasPunches.add( new Punch(100, 71));
		andreasPunches.add( new Punch(200, 72));
		andreasPunches.add( new Punch(300, 71));
		andreasPunches.add( new Punch(500, 72));
		andreasPunches.add( new Punch(600, 71));
		andreasPunches.add( new Punch(900, 72));
		cardAndreas.setPunches(andreasPunches);
		String cardStatus = competitorAndreas.processCard(cardAndreas, competition.getStages(), Competition.SVARTVITT_TYPE);
		System.out.println("Proccess card status for Andreas S" + cardStatus);
		assertEquals(competitorAndreas.getCardNumber(), competitorAndreas.getCard().getCardNumber());
		competition.calculateResults();
		results = competition.getResults();
		assertNotNull(results);
		totalResult = results.getTotalResult(COMP_CLASS_TO_TEST);
		assertNotNull(totalResult);
		System.out.println("Total results is: " + totalResult);
		assertEquals(EXPECTED_NUM_COMPETITORS, totalResult.getTotalTimeResult().size() );
		assertEquals(EXPECTED_NUM_STAGES, results.getAllStageResults(COMP_CLASS_TO_TEST).size() );
		assertEquals(EXPECTED_NUM_STAGES+1, results.getAllResults(COMP_CLASS_TO_TEST).size() );
		
		System.out.println("Check first entry in the total result, should be Andreas S");
		assertEquals( (Long)600L, totalResult.getTotalTimeResult().get(0).getStageTime() );
		assertEquals( (Integer)1, totalResult.getTotalTimeResult().get(0).getRank() );
		assertEquals( (Long)0L, totalResult.getTotalTimeResult().get(0).getStageTimesBack() );
		assertEquals(2079749,totalResult.getTotalTimeResult().get(0).getCardNumber() );
		
		System.out.println("Check first entry in each stage, should be Andreas S");
		for( int stageNumber = 1; stageNumber <= EXPECTED_NUM_STAGES; stageNumber++){
			Results stageResult = results.getStageResult(stageNumber, COMP_CLASS_TO_TEST);
			System.out.println("Stage " + stageNumber + " results: " + stageResult );
			assertEquals( (Long)(stageNumber*100L), stageResult.getStageResult().get(0).getStageTime() );
			assertEquals( (Integer)1, stageResult.getStageResult().get(0).getRank() );
			assertEquals( (Long)0L, stageResult.getStageResult().get(0).getStageTimesBack() );
			assertEquals(2079749,stageResult.getStageResult().get(0).getCardNumber() );
		}
		
		
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		System.out.println("Add results for competitor 2: Sverker G - 2078056");
		Competitor competitorSverker = competition.getCompetitors().getByCardNumber(2078056);
		assertEquals(2078056, competitorSverker.getCardNumber() );
		Card cardSverker = new Card();
		cardSverker.setCardNumber(2078056);
		cardSverker.setNumberOfPunches(6);
		List<Punch> sverkerPunches = new ArrayList<>();
		sverkerPunches.add( new Punch(10, 71));
		sverkerPunches.add( new Punch(20, 72));
		sverkerPunches.add( new Punch(30, 71));
		sverkerPunches.add( new Punch(50, 72));
		sverkerPunches.add( new Punch(60, 71));
		sverkerPunches.add( new Punch(90, 72));
		cardSverker.setPunches(sverkerPunches);
		cardStatus = competitorSverker.processCard(cardSverker, competition.getStages(), Competition.SVARTVITT_TYPE);
		System.out.println("Proccess card status for Sverker G" + cardStatus);
		assertEquals(competitorSverker.getCardNumber(), competitorSverker.getCard().getCardNumber());
		competition.calculateResults();
		results = competition.getResults();
		assertNotNull(results);
		totalResult = results.getTotalResult(COMP_CLASS_TO_TEST);
		assertNotNull(totalResult);
		System.out.println("Total results is: " + totalResult);
		assertEquals(EXPECTED_NUM_COMPETITORS, totalResult.getTotalTimeResult().size() );
		assertEquals(EXPECTED_NUM_STAGES, results.getAllStageResults(COMP_CLASS_TO_TEST).size() );
		assertEquals(EXPECTED_NUM_STAGES+1, results.getAllResults(COMP_CLASS_TO_TEST).size() );
		
		System.out.println("Check first entry in the total result, should be Sverker G");
		assertEquals( (Long)60L, totalResult.getTotalTimeResult().get(0).getStageTime() );
		assertEquals( (Integer)1, totalResult.getTotalTimeResult().get(0).getRank() );
		assertEquals( (Long)0L, totalResult.getTotalTimeResult().get(0).getStageTimesBack() );
		assertEquals(2078056,totalResult.getTotalTimeResult().get(0).getCardNumber() );
		
		System.out.println("Check second entry in the total result, should be Andreas S");
		assertEquals( (Long)600L, totalResult.getTotalTimeResult().get(1).getStageTime() );
		assertEquals( (Integer)2, totalResult.getTotalTimeResult().get(1).getRank() );
		assertEquals( (Long)540L, totalResult.getTotalTimeResult().get(1).getStageTimesBack() );
		assertEquals(2079749,totalResult.getTotalTimeResult().get(1).getCardNumber() );
		
		System.out.println("Check first entry in each stage, should be Sverker G");
		for( int stageNumber = 1; stageNumber <= EXPECTED_NUM_STAGES; stageNumber++){
			Results stageResult = results.getStageResult(stageNumber, COMP_CLASS_TO_TEST);
			System.out.println("Stage " + stageNumber + " results: " + stageResult );
			assertEquals( (Long)(stageNumber*10L), stageResult.getStageResult().get(0).getStageTime() );
			assertEquals( (Integer)1, stageResult.getStageResult().get(0).getRank() );
			assertEquals( (Long)0L, stageResult.getStageResult().get(0).getStageTimesBack() );
			assertEquals(2078056,stageResult.getStageResult().get(0).getCardNumber() );
		}
		
		System.out.println("Check second entry in each stage, should be Andreas S");
		for( int stageNumber = 1; stageNumber <= EXPECTED_NUM_STAGES; stageNumber++){
			Results stageResult = results.getStageResult(stageNumber, COMP_CLASS_TO_TEST);
			System.out.println("Stage " + stageNumber + " results: " + stageResult );
			assertEquals( (Long)(stageNumber*100L), stageResult.getStageResult().get(1).getStageTime() );
			assertEquals( (Integer)2, stageResult.getStageResult().get(1).getRank() );
			assertEquals( (Long)(stageNumber*90L), stageResult.getStageResult().get(1).getStageTimesBack() );
			assertEquals(2079749,stageResult.getStageResult().get(1).getCardNumber() );
		}

		
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		System.out.println("Add results for competitor 3: Släggan- 2078082. Only add results for 2 stages");
		Competitor competitorSledgeHammer = competition.getCompetitors().getByCardNumber(2078082);
		assertEquals(2078082, competitorSledgeHammer.getCardNumber() );
		Card cardSledgeHammer = new Card();
		cardSledgeHammer.setCardNumber(2078082);
		cardSledgeHammer.setNumberOfPunches(6);
		List<Punch> sledgeHammerPunches = new ArrayList<>();
		sledgeHammerPunches.add( new Punch(100, 71));
		sledgeHammerPunches.add( new Punch(150, 72));
		sledgeHammerPunches.add( new Punch(200, 71));
		sledgeHammerPunches.add( new Punch(210, 72));
		cardSledgeHammer.setPunches(sledgeHammerPunches);
		cardStatus = competitorSledgeHammer.processCard(cardSledgeHammer, competition.getStages(), Competition.SVARTVITT_TYPE);
		System.out.println("Proccess card status for Släggan" + cardStatus);
		assertEquals(competitorSledgeHammer.getCardNumber(), competitorSledgeHammer.getCard().getCardNumber());
		competition.calculateResults();
		results = competition.getResults();
		landscapeResults = competition.getResultLandscape();
		assertNotNull(results);
		totalResult = results.getTotalResult(COMP_CLASS_TO_TEST);
		assertNotNull(totalResult);
		System.out.println("Total results is: " + totalResult);
		assertEquals(EXPECTED_NUM_COMPETITORS, totalResult.getTotalTimeResult().size() );
		assertEquals(EXPECTED_NUM_STAGES, results.getAllStageResults(COMP_CLASS_TO_TEST).size() );
		assertEquals(EXPECTED_NUM_STAGES+1, results.getAllResults(COMP_CLASS_TO_TEST).size() );
		
		System.out.println("Check first entry in the total result, should be Sverker G");
		assertEquals( (Long)60L, totalResult.getTotalTimeResult().get(0).getStageTime() );
		assertEquals( (Integer)1, totalResult.getTotalTimeResult().get(0).getRank() );
		assertEquals( (Long)0L, totalResult.getTotalTimeResult().get(0).getStageTimesBack() );
		assertEquals(2078056,totalResult.getTotalTimeResult().get(0).getCardNumber() );
		
		System.out.println("Check second entry in the total result, should be Andreas S");
		assertEquals( (Long)600L, totalResult.getTotalTimeResult().get(1).getStageTime() );
		assertEquals( (Integer)2, totalResult.getTotalTimeResult().get(1).getRank() );
		assertEquals( (Long)540L, totalResult.getTotalTimeResult().get(1).getStageTimesBack() );
		assertEquals(2079749,totalResult.getTotalTimeResult().get(1).getCardNumber() );
		
		System.out.println("Check third entry in the total result, should be Släggan but with DNF result");
		assertEquals( (Long)Competition.COMPETITION_DNF, totalResult.getTotalTimeResult().get(2).getStageTime() );
		assertEquals( (Integer)Competition.RANK_DNF, totalResult.getTotalTimeResult().get(2).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, totalResult.getTotalTimeResult().get(2).getStageTimesBack() );
		assertEquals(2078082,totalResult.getTotalTimeResult().get(2).getCardNumber() );
		
		assertEquals(EXPECTED_NUM_COMPETITORS, landscapeResults.size());
		StageResult winnerTotalResult = landscapeResults.get(0).getStageResult().get(0);
		StageResult winnerStage1Result = landscapeResults.get(0).getStageResult().get(1);
		StageResult winnerStage2Result = landscapeResults.get(0).getStageResult().get(2);
		StageResult winnerStage3Result = landscapeResults.get(0).getStageResult().get(3);
		assertEquals(2078056, winnerTotalResult.getCardNumber() );
		assertEquals((Long)60L, winnerTotalResult.getStageTime() );
		assertEquals((Long)0L, winnerTotalResult.getStageTimesBack() );
		assertEquals((Integer)1, winnerTotalResult.getRank() );
		assertEquals(2078056, winnerStage1Result.getCardNumber() );
		assertEquals((Long)10L, winnerStage1Result.getStageTime() );
		assertEquals((Long)0L, winnerStage1Result.getStageTimesBack() );
		assertEquals((Integer)1, winnerStage1Result.getRank() );
		assertEquals(2078056, winnerStage2Result.getCardNumber() );
		assertEquals((Long)20L, winnerStage2Result.getStageTime() );
		assertEquals((Long)10L, winnerStage2Result.getStageTimesBack() );
		assertEquals((Integer)2, winnerStage2Result.getRank() );
		assertEquals(2078056, winnerStage3Result.getCardNumber() );
		assertEquals((Long)30L, winnerStage3Result.getStageTime() );
		assertEquals((Long)0L, winnerStage3Result.getStageTimesBack() );
		assertEquals((Integer)1, winnerStage3Result.getRank() );
		
		StageResult secondTotalResult = landscapeResults.get(1).getStageResult().get(0);
		StageResult secondStage1Result = landscapeResults.get(1).getStageResult().get(1);
		StageResult secondStage2Result = landscapeResults.get(1).getStageResult().get(2);
		StageResult secondStage3Result = landscapeResults.get(1).getStageResult().get(3);
		assertEquals(2079749, secondTotalResult.getCardNumber() );
		assertEquals((Long)600L, secondTotalResult.getStageTime() );
		assertEquals((Long)540L, secondTotalResult.getStageTimesBack() );
		assertEquals((Integer)2, secondTotalResult.getRank() );
		assertEquals(2079749, secondStage1Result.getCardNumber() );
		assertEquals((Long)100L, secondStage1Result.getStageTime() );
		assertEquals((Long)90L, secondStage1Result.getStageTimesBack() );
		assertEquals((Integer)3, secondStage1Result.getRank() );
		assertEquals(2079749, secondStage2Result.getCardNumber() );
		assertEquals((Long)200L, secondStage2Result.getStageTime() );
		assertEquals((Long)190L, secondStage2Result.getStageTimesBack() );
		assertEquals((Integer)3, secondStage2Result.getRank() );
		assertEquals(2079749, secondStage3Result.getCardNumber() );
		assertEquals((Long)300L, secondStage3Result.getStageTime() );
		assertEquals((Long)270L, secondStage3Result.getStageTimesBack() );
		assertEquals((Integer)2, secondStage3Result.getRank() );
		
		StageResult thirdTotalResult = landscapeResults.get(2).getStageResult().get(0);
		StageResult thirdStage1Result = landscapeResults.get(2).getStageResult().get(1);
		StageResult thirdStage2Result = landscapeResults.get(2).getStageResult().get(2);
		StageResult thirdStage3Result = landscapeResults.get(2).getStageResult().get(3);
		assertEquals(2078082, thirdTotalResult.getCardNumber() );
		assertEquals((Long)Competition.COMPETITION_DNF, thirdTotalResult.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, thirdTotalResult.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, thirdTotalResult.getRank() );
		assertEquals(2078082, thirdStage1Result.getCardNumber() );
		assertEquals((Long)50L, thirdStage1Result.getStageTime() );
		assertEquals((Long)40L, thirdStage1Result.getStageTimesBack() );
		assertEquals((Integer)2, thirdStage1Result.getRank() );
		assertEquals(2078082, thirdStage2Result.getCardNumber() );
		assertEquals((Long)10L, thirdStage2Result.getStageTime() );
		assertEquals((Long)0L, thirdStage2Result.getStageTimesBack() );
		assertEquals((Integer)1, thirdStage2Result.getRank() );
		assertEquals(2078082, thirdStage3Result.getCardNumber() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, thirdStage3Result.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, thirdStage3Result.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, thirdStage3Result.getRank() );
		
		for( Results competitorResult : landscapeResults.subList(3, landscapeResults.size())){
			int stageNumber = 0;
			for( StageResult competitorStageResult : competitorResult.getStageResult() ){
				if(stageNumber == 0){
					assertEquals((Long)Competition.NO_TIME_FOR_COMPETITION, competitorStageResult.getStageTime() );
				}
				else{
					assertEquals((Long)Competition.NO_TIME_FOR_STAGE, competitorStageResult.getStageTime() );
				}
				stageNumber++;
				assertEquals((Long)Competition.NO_TIME_FOR_STAGE, competitorStageResult.getStageTimesBack());
				assertEquals((Integer)Competition.RANK_DNF, competitorStageResult.getRank());
			}	
		}

		List<Results> resultLandscape = competition.getResultLandscape();
		for( Results lResult : resultLandscape){
			System.out.println( "resultLandscape " + lResult.getStageResult() );
		}
		for( Results cResult : results){
			System.out.println( "cResult " + cResult.getStageResult() );
		}
		
	}

}
