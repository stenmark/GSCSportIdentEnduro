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
		final int EXPECTED_NUM_COMPETITORS = 6;
		final int EXPECTED_NUM_STAGES = 3;
		
		//Init Competition with test parameters
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72");
		competition.getCompetitors().add("Andreas S", "2079749", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Peter B", "2065396", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Sverker G", "2078056", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Ingemar G", "2079747", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Släggan", "2078082", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Fruttberg", "2078040", "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.setCompetitionDate("2015-09-12");
		competition.setCompetitionType( Competition.SVART_VIT_TYPE);
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
		String cardStatus = competitorAndreas.processCard(cardAndreas, competition.getStages(), Competition.SVART_VIT_TYPE);
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
		cardStatus = competitorSverker.processCard(cardSverker, competition.getStages(), Competition.SVART_VIT_TYPE);
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
		cardStatus = competitorSledgeHammer.processCard(cardSledgeHammer, competition.getStages(), Competition.SVART_VIT_TYPE);
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
		
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		System.out.println("Add results for competitor 4: Peter B - 2065396. Set same stage time as Sverker on stage 2. And same total Time as Andreas S");
		Competitor competitorPeter = competition.getCompetitors().getByCardNumber(2065396);
		assertEquals(2065396, competitorPeter.getCardNumber() );
		Card cardPeter = new Card();
		cardPeter.setCardNumber(2065396);
		cardPeter.setNumberOfPunches(6);
		List<Punch> peterPunches = new ArrayList<>();
		peterPunches.add( new Punch(5, 71));
		peterPunches.add( new Punch(115, 72));
		peterPunches.add( new Punch(130, 71));
		peterPunches.add( new Punch(150, 72));
		peterPunches.add( new Punch(500, 71));
		peterPunches.add( new Punch(970, 72));
		cardPeter.setPunches(peterPunches);
		cardStatus = competitorPeter.processCard(cardPeter, competition.getStages(), Competition.SVART_VIT_TYPE);
		assertEquals(competitorPeter.getCardNumber(), competitorPeter.getCard().getCardNumber());
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
		
		System.out.println("Check third entry in the total result, should be Peter B. Should have same time and Rank as Andreas S");
		assertEquals( (Long)600L, totalResult.getTotalTimeResult().get(2).getStageTime() );
		assertEquals( (Integer)2, totalResult.getTotalTimeResult().get(2).getRank() );
		assertEquals( (Long)540L, totalResult.getTotalTimeResult().get(2).getStageTimesBack() );
		assertEquals(2065396,totalResult.getTotalTimeResult().get(2).getCardNumber() );

		System.out.println("Check fourth entry in the total result, should be Släggan but with DNF result");
		assertEquals( (Long)Competition.COMPETITION_DNF, totalResult.getTotalTimeResult().get(3).getStageTime() );
		assertEquals( (Integer)Competition.RANK_DNF, totalResult.getTotalTimeResult().get(3).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, totalResult.getTotalTimeResult().get(3).getStageTimesBack() );
		assertEquals(2078082,totalResult.getTotalTimeResult().get(3).getCardNumber() );

		
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		////////////////////////	////////////////////////	//////////////////////// 	//////////////////////// 
		System.out.println("Add results for competitor 5: Fruttberg - 2078040. Total time highest, check that rank is last");
		Competitor competitorFruttberg = competition.getCompetitors().getByCardNumber(2078040);
		assertEquals(2078040, competitorFruttberg.getCardNumber() );
		Card cardFruttberg = new Card();
		cardFruttberg.setCardNumber(2078040);
		cardFruttberg.setNumberOfPunches(6);
		List<Punch> fruttbergPunches = new ArrayList<>();
		fruttbergPunches.add( new Punch(250, 71));
		fruttbergPunches.add( new Punch(450, 72));
		fruttbergPunches.add( new Punch(550, 71));
		fruttbergPunches.add( new Punch(750, 72));
		fruttbergPunches.add( new Punch(1000, 71));
		fruttbergPunches.add( new Punch(1300, 72));
		cardFruttberg.setPunches(fruttbergPunches);
		cardStatus = competitorFruttberg.processCard(cardFruttberg, competition.getStages(), Competition.SVART_VIT_TYPE);
		assertEquals(competitorFruttberg.getCardNumber(), competitorFruttberg.getCard().getCardNumber());
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
		
		System.out.println("Check third entry in the total result, should be Peter B. Should have same time and Rank as Andreas S");
		assertEquals( (Long)600L, totalResult.getTotalTimeResult().get(2).getStageTime() );
		assertEquals( (Integer)2, totalResult.getTotalTimeResult().get(2).getRank() );
		assertEquals( (Long)540L, totalResult.getTotalTimeResult().get(2).getStageTimesBack() );
		assertEquals(2065396,totalResult.getTotalTimeResult().get(2).getCardNumber() );
		
		System.out.println("Check fourth entry in the total result, should be Fruttberg. Should be rank 4");
		assertEquals( (Long)700L, totalResult.getTotalTimeResult().get(3).getStageTime() );
		assertEquals( (Integer)4, totalResult.getTotalTimeResult().get(3).getRank() );
		assertEquals( (Long)640L, totalResult.getTotalTimeResult().get(3).getStageTimesBack() );
		assertEquals(2078040,totalResult.getTotalTimeResult().get(3).getCardNumber() );

		System.out.println("Check fifth entry in the total result, should be Släggan but with DNF result");
		assertEquals( (Long)Competition.COMPETITION_DNF, totalResult.getTotalTimeResult().get(4).getStageTime() );
		assertEquals( (Integer)Competition.RANK_DNF, totalResult.getTotalTimeResult().get(4).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, totalResult.getTotalTimeResult().get(4).getStageTimesBack() );
		assertEquals(2078082,totalResult.getTotalTimeResult().get(4).getCardNumber() );
		
		System.out.println("Check sixth entry in the total result, should be Ingemar but with \"no time result\"");
		assertEquals( (Long)Competition.NO_TIME_FOR_COMPETITION, totalResult.getTotalTimeResult().get(5).getStageTime() );
		assertEquals( (Integer)Competition.RANK_DNF, totalResult.getTotalTimeResult().get(5).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, totalResult.getTotalTimeResult().get(5).getStageTimesBack() );
		assertEquals(2079747,totalResult.getTotalTimeResult().get(5).getCardNumber() );
		
		System.out.println("Check first entry in each stage, should be Sverker G");
		for( int stageNumber = 1; stageNumber <= EXPECTED_NUM_STAGES; stageNumber++){
			Results stageResult = results.getStageResult(stageNumber, COMP_CLASS_TO_TEST);
			System.out.println("Stage " + stageNumber + " results: " + stageResult );
			//Sverker is not the winner on stage 2
			if( stageNumber == 2){ 
				assertEquals( (Long)(stageNumber*10L), stageResult.getStageResult().get(1).getStageTime() );
				assertEquals( (Integer)2, stageResult.getStageResult().get(1).getRank() );
				assertEquals( (Long)10L, stageResult.getStageResult().get(1).getStageTimesBack() );
				//Same time as Peter B on stage 2, Peter ends up in position 1 in the array but shall have the same rank as sverker
				assertEquals(2078056,stageResult.getStageResult().get(2).getCardNumber() );
			}
			else { 
				assertEquals( (Long)(stageNumber*10L), stageResult.getStageResult().get(0).getStageTime() ); 
				assertEquals( (Integer)1, stageResult.getStageResult().get(0).getRank() );
				assertEquals( (Long)0L, stageResult.getStageResult().get(0).getStageTimesBack() );
				assertEquals(2078056,stageResult.getStageResult().get(0).getCardNumber() );
			}	
		}
		
		System.out.println("Check contents of stage results");
		Results stage1Result = results.getStageResult(1, COMP_CLASS_TO_TEST);
		Results stage2Result = results.getStageResult(2, COMP_CLASS_TO_TEST);
		Results stage3Result = results.getStageResult(3, COMP_CLASS_TO_TEST);
		assertEquals("Stage 1", stage1Result.getTitle());
		assertEquals("Stage 2", stage2Result.getTitle());
		assertEquals("Stage 3", stage3Result.getTitle());
		assertEquals(EXPECTED_NUM_COMPETITORS, stage1Result.getStageResult().size());
		assertEquals(EXPECTED_NUM_COMPETITORS, stage2Result.getStageResult().size());
		assertEquals(EXPECTED_NUM_COMPETITORS, stage3Result.getStageResult().size());
		
		System.out.println("Check results for each stage Competitor Andreas S");
		assertEquals( (Long)(100L), stage1Result.getStageResult().get(2).getStageTime() ); 
		assertEquals( (Integer)3, stage1Result.getStageResult().get(2).getRank() );
		assertEquals( (Long)90L, stage1Result.getStageResult().get(2).getStageTimesBack() );
		assertEquals(2079749,stage1Result.getStageResult().get(2).getCardNumber() );
		assertEquals( (Long)(200L), stage2Result.getStageResult().get(3).getStageTime() ); 
		assertEquals( (Integer)4, stage2Result.getStageResult().get(3).getRank() );
		assertEquals( (Long)190L, stage2Result.getStageResult().get(3).getStageTimesBack() );
		assertEquals(2079749,stage2Result.getStageResult().get(3).getCardNumber() );
		assertEquals( (Long)(300L), stage3Result.getStageResult().get(1).getStageTime() ); 
		assertEquals( (Integer)2, stage3Result.getStageResult().get(1).getRank() );
		assertEquals( (Long)270L, stage3Result.getStageResult().get(1).getStageTimesBack() );
		assertEquals(2079749,stage3Result.getStageResult().get(1).getCardNumber() );
		
		System.out.println("Check results for each stage Competitor Peter B");
		assertEquals( (Long)(110L), stage1Result.getStageResult().get(3).getStageTime() ); 
		assertEquals( (Integer)4, stage1Result.getStageResult().get(3).getRank() );
		assertEquals( (Long)100L, stage1Result.getStageResult().get(3).getStageTimesBack() );
		assertEquals(2065396,stage1Result.getStageResult().get(3).getCardNumber() );
		assertEquals( (Long)(20L), stage2Result.getStageResult().get(1).getStageTime() ); 
		assertEquals( (Integer)2, stage2Result.getStageResult().get(1).getRank() );
		assertEquals( (Long)10L, stage2Result.getStageResult().get(1).getStageTimesBack() );
		assertEquals(2065396,stage2Result.getStageResult().get(1).getCardNumber() );
		assertEquals( (Long)(470L), stage3Result.getStageResult().get(3).getStageTime() ); 
		assertEquals( (Integer)4, stage3Result.getStageResult().get(3).getRank() );
		assertEquals( (Long)440L, stage3Result.getStageResult().get(3).getStageTimesBack() );
		assertEquals(2065396,stage3Result.getStageResult().get(3).getCardNumber() );
		
		System.out.println("Check results for each stage Competitor Fruttberg");
		assertEquals( (Long)(200L), stage1Result.getStageResult().get(4).getStageTime() ); 
		assertEquals( (Integer)5, stage1Result.getStageResult().get(4).getRank() );
		assertEquals( (Long)190L, stage1Result.getStageResult().get(4).getStageTimesBack() );
		assertEquals(2078040,stage1Result.getStageResult().get(4).getCardNumber() );
		assertEquals( (Long)(200L), stage2Result.getStageResult().get(4).getStageTime() ); 
		assertEquals( (Integer)4, stage2Result.getStageResult().get(4).getRank() );
		assertEquals( (Long)190L, stage2Result.getStageResult().get(4).getStageTimesBack() );
		assertEquals(2078040,stage2Result.getStageResult().get(4).getCardNumber() );
		assertEquals( (Long)(300L), stage3Result.getStageResult().get(2).getStageTime() ); 
		assertEquals( (Integer)2, stage3Result.getStageResult().get(2).getRank() );
		assertEquals( (Long)270L, stage3Result.getStageResult().get(2).getStageTimesBack() );
		assertEquals(2078040,stage3Result.getStageResult().get(2).getCardNumber() );
		
		System.out.println("Check results for each stage Competitor Släggan");
		assertEquals( (Long)(50L), stage1Result.getStageResult().get(1).getStageTime() ); 
		assertEquals( (Integer)2, stage1Result.getStageResult().get(1).getRank() );
		assertEquals( (Long)40L, stage1Result.getStageResult().get(1).getStageTimesBack() );
		assertEquals(2078082,stage1Result.getStageResult().get(1).getCardNumber() );
		assertEquals( (Long)(10L), stage2Result.getStageResult().get(0).getStageTime() ); 
		assertEquals( (Integer)1, stage2Result.getStageResult().get(0).getRank() );
		assertEquals( (Long)0L, stage2Result.getStageResult().get(0).getStageTimesBack() );
		assertEquals(2078082,stage2Result.getStageResult().get(0).getCardNumber() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage3Result.getStageResult().get(5).getStageTime() ); 
		assertEquals( (Integer)Competition.RANK_DNF, stage3Result.getStageResult().get(5).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage3Result.getStageResult().get(5).getStageTimesBack() );
		assertEquals(2078082,stage3Result.getStageResult().get(5).getCardNumber() );
		
		System.out.println("Check results for each stage Competitor Ingemar (No time on any stage)");
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage1Result.getStageResult().get(5).getStageTime() ); 
		assertEquals( (Integer)Competition.RANK_DNF, stage1Result.getStageResult().get(5).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage1Result.getStageResult().get(5).getStageTimesBack() );
		assertEquals(2079747,stage1Result.getStageResult().get(5).getCardNumber() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage2Result.getStageResult().get(5).getStageTime() ); 
		assertEquals( (Integer)Competition.RANK_DNF, stage2Result.getStageResult().get(5).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage2Result.getStageResult().get(5).getStageTimesBack() );
		assertEquals(2079747,stage2Result.getStageResult().get(5).getCardNumber() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage3Result.getStageResult().get(4).getStageTime() ); 
		assertEquals( (Integer)Competition.RANK_DNF, stage3Result.getStageResult().get(4).getRank() );
		assertEquals( (Long)Competition.NO_TIME_FOR_STAGE, stage3Result.getStageResult().get(4).getStageTimesBack() );
		assertEquals(2079747,stage3Result.getStageResult().get(4).getCardNumber() );
		
		System.out.println("Check landscape results");
		landscapeResults = competition.getResultLandscape();
		assertNotNull(landscapeResults);
		assertEquals(EXPECTED_NUM_COMPETITORS, landscapeResults.size());
		
		assertEquals(EXPECTED_NUM_COMPETITORS, landscapeResults.size());
		winnerTotalResult = landscapeResults.get(0).getStageResult().get(0);
		winnerStage1Result = landscapeResults.get(0).getStageResult().get(1);
		winnerStage2Result = landscapeResults.get(0).getStageResult().get(2);
		winnerStage3Result = landscapeResults.get(0).getStageResult().get(3);
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
		
		secondTotalResult = landscapeResults.get(1).getStageResult().get(0);
		secondStage1Result = landscapeResults.get(1).getStageResult().get(1);
		secondStage2Result = landscapeResults.get(1).getStageResult().get(2);
		secondStage3Result = landscapeResults.get(1).getStageResult().get(3);
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
		assertEquals((Integer)4, secondStage2Result.getRank() );
		assertEquals(2079749, secondStage3Result.getCardNumber() );
		assertEquals((Long)300L, secondStage3Result.getStageTime() );
		assertEquals((Long)270L, secondStage3Result.getStageTimesBack() );
		assertEquals((Integer)2, secondStage3Result.getRank() );
		
		thirdTotalResult = landscapeResults.get(2).getStageResult().get(0);
		thirdStage1Result = landscapeResults.get(2).getStageResult().get(1);
		thirdStage2Result = landscapeResults.get(2).getStageResult().get(2);
		thirdStage3Result = landscapeResults.get(2).getStageResult().get(3);
		assertEquals(2065396, thirdTotalResult.getCardNumber() );
		assertEquals((Long)600L, thirdTotalResult.getStageTime() );
		assertEquals((Long)540L, thirdTotalResult.getStageTimesBack() );
		assertEquals((Integer)2, thirdTotalResult.getRank() );
		assertEquals(2065396, thirdStage1Result.getCardNumber() );
		assertEquals((Long)110L, thirdStage1Result.getStageTime() );
		assertEquals((Long)100L, thirdStage1Result.getStageTimesBack() );
		assertEquals((Integer)4, thirdStage1Result.getRank() );
		assertEquals(2065396, thirdStage2Result.getCardNumber() );
		assertEquals((Long)20L, thirdStage2Result.getStageTime() );
		assertEquals((Long)10L, thirdStage2Result.getStageTimesBack() );
		assertEquals((Integer)2, thirdStage2Result.getRank() );
		assertEquals(2065396, thirdStage3Result.getCardNumber() );
		assertEquals((Long)470L, thirdStage3Result.getStageTime() );
		assertEquals((Long)440L, thirdStage3Result.getStageTimesBack() );
		assertEquals((Integer)4, thirdStage3Result.getRank() );
		
		StageResult fourthTotalResult = landscapeResults.get(3).getStageResult().get(0);
		StageResult fourthStage1Result = landscapeResults.get(3).getStageResult().get(1);
		StageResult fourthStage2Result = landscapeResults.get(3).getStageResult().get(2);
		StageResult fourthStage3Result = landscapeResults.get(3).getStageResult().get(3);
		assertEquals(2078040, fourthTotalResult.getCardNumber() );
		assertEquals((Long)700L, fourthTotalResult.getStageTime() );
		assertEquals((Long)640L, fourthTotalResult.getStageTimesBack() );
		assertEquals((Integer)4, fourthTotalResult.getRank() );
		assertEquals(2078040, fourthStage1Result.getCardNumber() );
		assertEquals((Long)200L, fourthStage1Result.getStageTime() );
		assertEquals((Long)190L, fourthStage1Result.getStageTimesBack() );
		assertEquals((Integer)5, fourthStage1Result.getRank() );
		assertEquals(2078040, fourthStage2Result.getCardNumber() );
		assertEquals((Long)200L, fourthStage2Result.getStageTime() );
		assertEquals((Long)190L, fourthStage2Result.getStageTimesBack() );
		assertEquals((Integer)4, fourthStage2Result.getRank() );
		assertEquals(2078040, fourthStage3Result.getCardNumber() );
		assertEquals((Long)300L, fourthStage3Result.getStageTime() );
		assertEquals((Long)270L, fourthStage3Result.getStageTimesBack() );
		assertEquals((Integer)2, fourthStage3Result.getRank() );
		
		StageResult fifthTotalResult = landscapeResults.get(4).getStageResult().get(0);
		StageResult fifthStage1Result = landscapeResults.get(4).getStageResult().get(1);
		StageResult fifthStage2Result = landscapeResults.get(4).getStageResult().get(2);
		StageResult fifthStage3Result = landscapeResults.get(4).getStageResult().get(3);
		assertEquals(2078082, fifthTotalResult.getCardNumber() );
		assertEquals((Long)Competition.COMPETITION_DNF, fifthTotalResult.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, fifthTotalResult.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, fifthTotalResult.getRank() );
		assertEquals(2078082, fifthStage1Result.getCardNumber() );
		assertEquals((Long)50L, fifthStage1Result.getStageTime() );
		assertEquals((Long)40L, fifthStage1Result.getStageTimesBack() );
		assertEquals((Integer)2, fifthStage1Result.getRank() );
		assertEquals(2078082, fifthStage2Result.getCardNumber() );
		assertEquals((Long)10L, fifthStage2Result.getStageTime() );
		assertEquals((Long)0L, fifthStage2Result.getStageTimesBack() );
		assertEquals((Integer)1, fifthStage2Result.getRank() );
		assertEquals(2078082, fifthStage3Result.getCardNumber() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, fifthStage3Result.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, fifthStage3Result.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, fifthStage3Result.getRank() );
		
		StageResult sixthTotalResult = landscapeResults.get(5).getStageResult().get(0);
		StageResult sixthStage1Result = landscapeResults.get(5).getStageResult().get(1);
		StageResult sixthStage2Result = landscapeResults.get(5).getStageResult().get(2);
		StageResult sixthStage3Result = landscapeResults.get(5).getStageResult().get(3);
		assertEquals(2079747, sixthTotalResult.getCardNumber() );
		assertEquals((Long)Competition.NO_TIME_FOR_COMPETITION, sixthTotalResult.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, sixthTotalResult.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, sixthTotalResult.getRank() );
		assertEquals(2079747, sixthStage1Result.getCardNumber() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, sixthStage1Result.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, sixthStage1Result.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, sixthStage1Result.getRank() );
		assertEquals(2079747, sixthStage2Result.getCardNumber() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, sixthStage2Result.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, sixthStage2Result.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, sixthStage2Result.getRank() );
		assertEquals(2079747, sixthStage3Result.getCardNumber() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, sixthStage3Result.getStageTime() );
		assertEquals((Long)Competition.NO_TIME_FOR_STAGE, sixthStage3Result.getStageTimesBack() );
		assertEquals((Integer)Competition.RANK_DNF, sixthStage3Result.getRank() );
		
	}

}
