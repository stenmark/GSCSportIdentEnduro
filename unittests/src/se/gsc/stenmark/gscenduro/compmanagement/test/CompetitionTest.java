package se.gsc.stenmark.gscenduro.compmanagement.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;
import se.gsc.stenmark.gscenduro.SporIdent.test.SiDriverTest;
import se.gsc.stenmark.gscenduro.SporIdent.test.driverStubs.UsbDriverStub;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.ResultList;
import se.gsc.stenmark.gscenduro.compmanagement.Results;
import se.gsc.stenmark.gscenduro.compmanagement.StageResult;

public class CompetitionTest {

	@Test
	/**
	 * Test with reasl SIAC data from Stenungsund race. 
	 * Some competitors went into the Goal antenna before the start punch,
	 * which lead to negative time and punches needed to be removed manually.
	 * This test uses these real SIAC card readout to verify that the app can remove such punches automatically
	 * @throws Exception
	 */
	public void testCompetitorWithGoalAsFirstPunch() throws Exception{
		System.out.println("START testCompetitorWithGoalAsFirstPunch");
		final String COMP_CLASS_TO_TEST = "";
		
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72,71,72,71,72,71,72");
		competition.setCompetitionDate("2016-05-14");
		competition.setCompetitionType( Competition.SVART_VIT_TYPE);
		competition.setCompetitionName("Competition SvartVitt real test");
		readCompetitorsFromFile( "stenungsund_card_with_goal_as_first", COMP_CLASS_TO_TEST, competition);
		
		List<Card> cardList = readCardsFromFiles( "stenungsund_card_with_goal_as_first");
		for( Card card : cardList){
			Competitor currentCompetitor = competition.getCompetitors().getByCardNumber(card.getCardNumber());
			assertNotNull("Could not get competitor for cardnumber "+ card.getCardNumber(), currentCompetitor);
			currentCompetitor.processCard(card, competition.getStages(), Competition.SVART_VIT_TYPE);
		}
		
		competition.calculateResults();
		List<Results> resultList = competition.getResultLandscape();
		
		for( int competitorRank = 0; competitorRank < competition.getCompetitors().size(); competitorRank++){
			int cardNumber = resultList.get(competitorRank).getStageResult().get(0).getCardNumber();
			String name = competition.getCompetitors().getByCardNumber(cardNumber).getName();
			System.out.println(name + "  " + cardNumber);
				
			long totalTime = resultList.get(competitorRank).getStageResult().get(0).getStageTime();
			long stage1Time = resultList.get(competitorRank).getStageResult().get(1).getStageTime();
			long stage2Time = resultList.get(competitorRank).getStageResult().get(2).getStageTime();
			long stage3Time = resultList.get(competitorRank).getStageResult().get(3).getStageTime();
			long stage4Time = resultList.get(competitorRank).getStageResult().get(4).getStageTime();
			long stage5Time = resultList.get(competitorRank).getStageResult().get(5).getStageTime();
			long stage6Time = resultList.get(competitorRank).getStageResult().get(6).getStageTime();
						
			String resultAsString = name + "," + cardNumber + "," + totalTime + "," + stage1Time + "," + stage2Time + "," + stage3Time + "," + stage4Time + "," +stage5Time + "," +stage6Time;
			System.out.println(resultAsString);
			//Hardcode the check for the two cardnumbers
			if( cardNumber == 8633682){
				assertEquals("Fassberg,8633682,5000000,98,10000000,10000000,10000000,10000000,10000000", resultAsString);
			}
			else if( cardNumber == 8633672){
				assertEquals("EvilAs,8633672,544,123,45,71,135,88,82", resultAsString);
			}
			else{
				fail("Unknown cardnumber");
			}
			
			if( stage1Time+stage2Time+stage3Time+stage4Time+stage5Time+stage6Time > 40000){
				assertEquals(totalTime, 5000000);
			}
			else{
				assertEquals(totalTime, stage1Time+stage2Time+stage3Time+stage4Time+stage5Time+stage6Time);
			}
		}

	}
	
	@Test 
	public void testCompetitionFromRealCardData() throws Exception{
		System.out.println("START testCompetitionFromRealCardData");
		final String COMP_CLASS_TO_TEST = "";
		
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72,71,72,71,72,71,72");
		competition.setCompetitionDate("2016-04-10");
		competition.setCompetitionType( Competition.SVART_VIT_TYPE);
		competition.setCompetitionName("Competition SvartVitt real test");
		readCompetitorsFromFile("lackareback_competitionData",COMP_CLASS_TO_TEST, competition);
		
		List<Card> cardList = readCardsFromFiles( "lackareback_cardData");
		for( Card card : cardList){
			Competitor currentCompetitor = competition.getCompetitors().getByCardNumber(card.getCardNumber());
			assertNotNull("Could not get competitor for cardnumber "+ card.getCardNumber(), currentCompetitor);
			currentCompetitor.processCard(card, competition.getStages(), Competition.SVART_VIT_TYPE);
		}
		
		competition.calculateResults();
		List<Results> resultList = competition.getResultLandscape();
		List<String> expectedResultsData = readExpectedCompResults();
		for( int competitorRank = 0; competitorRank < competition.getCompetitors().size(); competitorRank++){
			int cardNumber = resultList.get(competitorRank).getStageResult().get(0).getCardNumber();
			String name = competition.getCompetitors().getByCardNumber(cardNumber).getName();
			System.out.println(name + "  " + cardNumber);
				
			long totalTime = resultList.get(competitorRank).getStageResult().get(0).getStageTime();
			long stage1Time = resultList.get(competitorRank).getStageResult().get(1).getStageTime();
			long stage2Time = resultList.get(competitorRank).getStageResult().get(2).getStageTime();
			long stage3Time = resultList.get(competitorRank).getStageResult().get(3).getStageTime();
			long stage4Time = resultList.get(competitorRank).getStageResult().get(4).getStageTime();
			long stage5Time = resultList.get(competitorRank).getStageResult().get(5).getStageTime();
			long stage6Time = resultList.get(competitorRank).getStageResult().get(6).getStageTime();
			
			String resultAsString = name + "," + cardNumber + "," + totalTime + "," + stage1Time + "," + stage2Time + "," + stage3Time + "," + stage4Time + "," +stage5Time + "," +stage6Time;
			String expectedResults = expectedResultsData.get(competitorRank);
			System.out.println("Calculated Data: " + resultAsString);
			System.out.println("Expected Data:   " + expectedResults);
			assertEquals(expectedResults, resultAsString);
			if( stage1Time+stage2Time+stage3Time+stage4Time+stage5Time+stage6Time > 40000){
				assertEquals(totalTime, 5000000);
			}
			else{
				assertEquals(totalTime, stage1Time+stage2Time+stage3Time+stage4Time+stage5Time+stage6Time);
			}

		}	
	}
	
	@Test
	public void testSlowestAndFastestTimeOnStage() {
		final String COMP_CLASS_TO_TEST = "";
		final int EXPECTED_NUM_STAGES = 5;
		final int STAGE1_START_TIME = 100;
		final int STAGE2_START_TIME = 500;
		final int STAGE3_START_TIME = 1000;
		final int STAGE4_START_TIME = 1500;
		final int STAGE5_START_TIME = 2000;
		
		//Init Competition with test parameters
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72,71,72,71,72");
		competition.getCompetitors().add("Andreas S", 2079749, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Peter B", 2065396, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Sverker G", 2078056, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Ingemar G", 2079747, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Släggan", 2078082, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Fruttberg", 2078040, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.setCompetitionDate("2015-09-12");
		competition.setCompetitionType( Competition.SVART_VIT_TYPE);
		competition.setCompetitionName("Competition test name");
		
		//Assert some basic competition fields
		assertEquals(EXPECTED_NUM_STAGES, competition.getStages().size() );
		assertEquals( 71, competition.getStages().get(0).getStart() );
		assertEquals( 72, competition.getStages().get(0).getFinish() );
		assertEquals( 71, competition.getStages().get(1).getStart() );
		assertEquals( 72, competition.getStages().get(1).getFinish() );

		System.out.println("Add results for competitor 1: Andreas S - 2079749");
		Competitor competitorAndreas = competition.getCompetitors().getByCardNumber(2079749);
		assertEquals(2079749, competitorAndreas.getCardNumber() );
		Card cardAndreas = new Card();
		cardAndreas.setCardNumber(2079749);
		cardAndreas.setNumberOfPunches(6);
		List<Punch> andreasPunches = new ArrayList<>();
		andreasPunches.add( new Punch(STAGE1_START_TIME, 71));
		andreasPunches.add( new Punch(STAGE1_START_TIME+123, 72));
		andreasPunches.add( new Punch(STAGE2_START_TIME, 71));
		andreasPunches.add( new Punch(STAGE2_START_TIME+85, 72));
		andreasPunches.add( new Punch(STAGE3_START_TIME, 71));
		andreasPunches.add( new Punch(STAGE3_START_TIME+190, 72));
		andreasPunches.add( new Punch(STAGE4_START_TIME, 71));
		andreasPunches.add( new Punch(STAGE4_START_TIME+55, 72));
		andreasPunches.add( new Punch(STAGE5_START_TIME, 71));
		andreasPunches.add( new Punch(STAGE5_START_TIME+122, 72));
		cardAndreas.setPunches(andreasPunches);
		competitorAndreas.processCard(cardAndreas, competition.getStages(), Competition.SVART_VIT_TYPE);
		
		Competitor competitorSverker = competition.getCompetitors().getByCardNumber(2078056);
		assertEquals(2078056, competitorSverker.getCardNumber() );
		Card cardSverker = new Card();
		cardSverker.setCardNumber(2078056);
		cardSverker.setNumberOfPunches(6);
		List<Punch> sverkerPunches = new ArrayList<>();
		sverkerPunches.add( new Punch(STAGE1_START_TIME, 71));
		sverkerPunches.add( new Punch(STAGE1_START_TIME+127, 72));
		sverkerPunches.add( new Punch(STAGE2_START_TIME, 71));
		sverkerPunches.add( new Punch(STAGE2_START_TIME+88, 72));
		sverkerPunches.add( new Punch(STAGE3_START_TIME, 71));
		sverkerPunches.add( new Punch(STAGE3_START_TIME+183, 72));
		sverkerPunches.add( new Punch(STAGE4_START_TIME, 71));
		sverkerPunches.add( new Punch(STAGE4_START_TIME+295, 72));
		sverkerPunches.add( new Punch(STAGE5_START_TIME, 71));
		sverkerPunches.add( new Punch(STAGE5_START_TIME+150, 72));
		cardSverker.setPunches(sverkerPunches);
		competitorSverker.processCard(cardSverker, competition.getStages(), Competition.SVART_VIT_TYPE);

		Competitor competitorSledgeHammer = competition.getCompetitors().getByCardNumber(2078082);
		assertEquals(2078082, competitorSledgeHammer.getCardNumber() );
		Card cardSledgeHammer = new Card();
		cardSledgeHammer.setCardNumber(2078082);
		cardSledgeHammer.setNumberOfPunches(6);
		List<Punch> sledgeHammerPunches = new ArrayList<>();
		sledgeHammerPunches.add( new Punch(STAGE1_START_TIME, 71));
		sledgeHammerPunches.add( new Punch(STAGE1_START_TIME+220, 72));
		sledgeHammerPunches.add( new Punch(STAGE2_START_TIME, 71));
		sledgeHammerPunches.add( new Punch(STAGE2_START_TIME+80, 72));
		sledgeHammerPunches.add( new Punch(STAGE3_START_TIME, 71));
		sledgeHammerPunches.add( new Punch(STAGE3_START_TIME+188, 72));
		sledgeHammerPunches.add( new Punch(STAGE4_START_TIME, 71));
		sledgeHammerPunches.add( new Punch(STAGE4_START_TIME+145, 72));
		sledgeHammerPunches.add( new Punch(STAGE5_START_TIME, 71));
		sledgeHammerPunches.add( new Punch(STAGE5_START_TIME+127, 72));
		cardSledgeHammer.setPunches(sledgeHammerPunches);
		competitorSledgeHammer.processCard(cardSledgeHammer, competition.getStages(), Competition.SVART_VIT_TYPE);
		
		Competitor competitorPeter = competition.getCompetitors().getByCardNumber(2065396);
		assertEquals(2065396, competitorPeter.getCardNumber() );
		Card cardPeter = new Card();
		cardPeter.setCardNumber(2065396);
		cardPeter.setNumberOfPunches(6);
		List<Punch> peterPunches = new ArrayList<>();
		peterPunches.add( new Punch(STAGE1_START_TIME, 71));
		peterPunches.add( new Punch(STAGE1_START_TIME+118, 72));
		peterPunches.add( new Punch(STAGE2_START_TIME, 71));
		peterPunches.add( new Punch(STAGE2_START_TIME+150, 72));
		peterPunches.add( new Punch(STAGE3_START_TIME, 71));
		peterPunches.add( new Punch(STAGE3_START_TIME+175, 72));
		peterPunches.add( new Punch(STAGE4_START_TIME, 71));
		peterPunches.add( new Punch(STAGE4_START_TIME+165, 72));
		peterPunches.add( new Punch(STAGE5_START_TIME, 71));
		peterPunches.add( new Punch(STAGE5_START_TIME+119, 72));
		cardPeter.setPunches(peterPunches);
		competitorPeter.processCard(cardPeter, competition.getStages(), Competition.SVART_VIT_TYPE);
	
		Competitor competitorFruttberg = competition.getCompetitors().getByCardNumber(2078040);
		assertEquals(2078040, competitorFruttberg.getCardNumber() );
		Card cardFruttberg = new Card();
		cardFruttberg.setCardNumber(2078040);
		cardFruttberg.setNumberOfPunches(6);
		List<Punch> fruttbergPunches = new ArrayList<>();
		fruttbergPunches.add( new Punch(STAGE1_START_TIME, 71));
		fruttbergPunches.add( new Punch(STAGE1_START_TIME+135, 72));
		fruttbergPunches.add( new Punch(STAGE2_START_TIME, 71));
		fruttbergPunches.add( new Punch(STAGE2_START_TIME+90, 72));
		fruttbergPunches.add( new Punch(STAGE3_START_TIME, 71));
		fruttbergPunches.add( new Punch(STAGE3_START_TIME+170, 72));
		fruttbergPunches.add( new Punch(STAGE4_START_TIME, 71));
		fruttbergPunches.add( new Punch(STAGE4_START_TIME+153, 72));
		fruttbergPunches.add( new Punch(STAGE5_START_TIME, 71));
		fruttbergPunches.add( new Punch(STAGE5_START_TIME+121, 72));
		cardFruttberg.setPunches(fruttbergPunches);
		competitorFruttberg.processCard(cardFruttberg, competition.getStages(), Competition.SVART_VIT_TYPE);
		
		competition.calculateResults();
		
		Long slowestOnStage1Filter = competition.calculateSlowestOnStage(COMP_CLASS_TO_TEST, 1);
		System.out.println("Slowest on stage 1 with filter " + slowestOnStage1Filter);
		Long fastestOnStage1 = competition.getFastestOnStage(COMP_CLASS_TO_TEST, 1);
		System.out.println("Fastest on stage 1 " + fastestOnStage1);
		
		Long slowestOnStage2Filter = competition.calculateSlowestOnStage(COMP_CLASS_TO_TEST, 2);
		System.out.println("Slowest on stage 2 with filter  " + slowestOnStage2Filter);
		Long fastestOnStage2 = competition.getFastestOnStage(COMP_CLASS_TO_TEST, 2);
		System.out.println("Fastest on stage 2 " + fastestOnStage2);
		
		Long slowestOnStage3Filter = competition.calculateSlowestOnStage(COMP_CLASS_TO_TEST, 3);
		System.out.println("Slowest on stage 3 with filter  " + slowestOnStage3Filter);
		Long fastestOnStage3 = competition.getFastestOnStage(COMP_CLASS_TO_TEST, 3);
		System.out.println("Fastest on stage 3  " + fastestOnStage3);
		
		
		Competitor competitorIngemar = competition.getCompetitors().getByCardNumber(2079747);
		assertEquals(2079747, competitorIngemar.getCardNumber() );
		Card cardIngemar = new Card();
		cardIngemar.setCardNumber(2079747);
		cardIngemar.setNumberOfPunches(6);
		List<Punch> ingemarPunches = new ArrayList<>();
		ingemarPunches.add( new Punch(STAGE1_START_TIME, 71));
		ingemarPunches.add( new Punch(STAGE1_START_TIME+137, 72));
		ingemarPunches.add( new Punch(STAGE2_START_TIME, 71));
		ingemarPunches.add( new Punch(STAGE2_START_TIME+99, 72));
		ingemarPunches.add( new Punch(STAGE3_START_TIME, 71));
		ingemarPunches.add( new Punch(STAGE3_START_TIME+172, 72));
		ingemarPunches.add( new Punch(STAGE4_START_TIME, 71));
		ingemarPunches.add( new Punch(STAGE4_START_TIME+162, 72));
		ingemarPunches.add( new Punch(STAGE5_START_TIME, 71));
		ingemarPunches.add( new Punch(STAGE5_START_TIME+120, 72));
		cardIngemar.setPunches(ingemarPunches);
		competitorIngemar.processCard(cardIngemar, competition.getStages(), Competition.SVART_VIT_TYPE);
		
		competition.calculateResults();
	
		Long slowestOnStage4Filter = competition.calculateSlowestOnStage(COMP_CLASS_TO_TEST, 4);
		System.out.println("Slowest on stage 4 with filter  " + slowestOnStage4Filter);
		Long fastestOnStage4 = competition.getFastestOnStage(COMP_CLASS_TO_TEST, 4);
		System.out.println("Fastest on stage 4 " + fastestOnStage4);
		
		Long slowestOnStage5Filter = competition.calculateSlowestOnStage(COMP_CLASS_TO_TEST, 5);
		System.out.println("Slowest on stage 5 with filter  " + slowestOnStage5Filter);
		Long fastestOnStage5 = competition.getFastestOnStage(COMP_CLASS_TO_TEST, 5);
		System.out.println("Fastest on stage 5  " + fastestOnStage5);
		
		//Stage 1 and Stage 2 just test normal functionality
		assertEquals((Long)135L, slowestOnStage1Filter);
		assertEquals((Long)118L, fastestOnStage1);
		assertEquals((Long)90L, slowestOnStage2Filter);
		assertEquals((Long)80L, fastestOnStage2);
		
		//Stage3 all competitors within the same time deviation, no competitor shall be filtered out
		assertEquals((Long)190L, slowestOnStage3Filter);
		assertEquals((Long)170L, fastestOnStage3);
		
		//Stage4 test that only bottom half of the list it filtered for big time deviations. If the winner is much faster than the rest, we shall not filter the rest out...
		assertEquals((Long)165L, slowestOnStage4Filter);
		assertEquals((Long)55L, fastestOnStage4);
		
		//Stage5 set all competitors within a very small timedelta (Seems common in "real" races). Make sure that timedelta cutoff works in filter.
		assertEquals((Long)127L, slowestOnStage5Filter);
		assertEquals((Long)119L, fastestOnStage5);
	}

	
	
	@Test
	public void testDoublePunch(){
		final int EXPECTED_NUM_STAGES = 4;
		final int EXPECTED_NUM_COMPETITORS = 1;
		final String COMP_CLASS_TO_TEST = "";
		
		//Init Competition with test parameters
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72,71,72");
		competition.getCompetitors().add("Andreas S", 2079749, COMP_CLASS_TO_TEST, "", "0", "0", 0);
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
		assertEquals( 71, competition.getStages().get(3).getStart() );
		assertEquals( 72, competition.getStages().get(3).getFinish() );

		assertEquals(EXPECTED_NUM_COMPETITORS, competition.getCompetitors().getCompetitors().size() );
		assertEquals(1, competition.getCompetitors().getCompetitorClasses().size());
		assertEquals( COMP_CLASS_TO_TEST, competition.getCompetitors().getCompetitorClasses().get(0) );
		
		System.out.println("Add results with double punches for competitor 1: Andreas S - 2079749");
		Competitor competitorAndreas = competition.getCompetitors().getByCardNumber(2079749);
		assertEquals(2079749, competitorAndreas.getCardNumber() );
		Card cardAndreas = new Card();
		cardAndreas.setCardNumber(2079749);
		cardAndreas.setNumberOfPunches(6);
		List<Punch> andreasPunches = new ArrayList<>();
		//SS1 Double punch start Should be 90 seconds
		andreasPunches.add( new Punch(100, 71));
		andreasPunches.add( new Punch(110, 71));
		andreasPunches.add( new Punch(200, 72));
		
		//SS2 no double punches  Should be 200 seconds
		andreasPunches.add( new Punch(300, 71));
		andreasPunches.add( new Punch(500, 72));
		
		//SS3 double punch finish  Should be 300 seconds
		andreasPunches.add( new Punch(600, 71));
		andreasPunches.add( new Punch(900, 72));
		andreasPunches.add( new Punch(912, 72));
		
		//SS4 double punch both start and finish should be 177 seconds
		andreasPunches.add( new Punch(1200, 71));
		andreasPunches.add( new Punch(1223, 71));
		andreasPunches.add( new Punch(1400, 72));
		andreasPunches.add( new Punch(1414, 72));
		cardAndreas.setPunches(andreasPunches);
		String cardStatus = competitorAndreas.processCard(cardAndreas, competition.getStages(), Competition.SVART_VIT_TYPE);
		System.out.println("Proccess card status for Andreas S: " + cardStatus);
		assertEquals(competitorAndreas.getCardNumber(), competitorAndreas.getCard().getCardNumber());
		
		competition.calculateResults();
		ResultList<Results> results = competition.getResults();
		assertNotNull(results);
		Results totalResult = results.getTotalResult(COMP_CLASS_TO_TEST);
		assertNotNull(totalResult);
		System.out.println("Total results is: " + totalResult);
		assertEquals(EXPECTED_NUM_COMPETITORS, totalResult.getTotalTimeResult().size() );
		assertEquals(EXPECTED_NUM_STAGES, results.getAllStageResults(COMP_CLASS_TO_TEST).size() );
		assertEquals(EXPECTED_NUM_STAGES+1, results.getAllResults(COMP_CLASS_TO_TEST).size() );
		
		System.out.println("Check first entry in the total result, should be Andreas S");
		assertEquals( (Long)767L, totalResult.getTotalTimeResult().get(0).getStageTime() );
		assertEquals( (Integer)1, totalResult.getTotalTimeResult().get(0).getRank() );
		assertEquals( (Long)0L, totalResult.getTotalTimeResult().get(0).getStageTimesBack() );
		assertEquals(2079749,totalResult.getTotalTimeResult().get(0).getCardNumber() );
		
		Results stage1Result = results.getStageResult(1, COMP_CLASS_TO_TEST);
		Results stage2Result = results.getStageResult(2, COMP_CLASS_TO_TEST);
		Results stage3Result = results.getStageResult(3, COMP_CLASS_TO_TEST);
		Results stage4Result = results.getStageResult(4, COMP_CLASS_TO_TEST);
		
		assertEquals( (Long)90L, stage1Result.getStageResult().get(0).getStageTime() );
		assertEquals( (Integer)1, stage1Result.getStageResult().get(0).getRank() );
		assertEquals( (Long)0L, stage1Result.getStageResult().get(0).getStageTimesBack() );
		assertEquals(2079749,stage1Result.getStageResult().get(0).getCardNumber() );
		
		assertEquals( (Long)200L, stage2Result.getStageResult().get(0).getStageTime() );
		assertEquals( (Integer)1, stage2Result.getStageResult().get(0).getRank() );
		assertEquals( (Long)0L, stage2Result.getStageResult().get(0).getStageTimesBack() );
		assertEquals(2079749,stage2Result.getStageResult().get(0).getCardNumber() );
		
		assertEquals( (Long)300L, stage3Result.getStageResult().get(0).getStageTime() );
		assertEquals( (Integer)1, stage3Result.getStageResult().get(0).getRank() );
		assertEquals( (Long)0L, stage3Result.getStageResult().get(0).getStageTimesBack() );
		assertEquals(2079749,stage3Result.getStageResult().get(0).getCardNumber() );
		
		assertEquals( (Long)177L, stage4Result.getStageResult().get(0).getStageTime() );
		assertEquals( (Integer)1, stage4Result.getStageResult().get(0).getRank() );
		assertEquals( (Long)0L, stage4Result.getStageResult().get(0).getStageTimesBack() );
		assertEquals(2079749,stage4Result.getStageResult().get(0).getCardNumber() );
	}
	
	@Test
	public void testFullCompetition() {
		final String COMP_CLASS_TO_TEST = "";
		final int EXPECTED_NUM_COMPETITORS = 6;
		final int EXPECTED_NUM_STAGES = 3;
		
		//Init Competition with test parameters
		Competition competition = new CompetiotnStub();
		competition.getStages().importStages("71,72,71,72,71,72");
		competition.getCompetitors().add("Andreas S", 2079749, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Peter B", 2065396, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Sverker G", 2078056, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Ingemar G", 2079747, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Släggan", 2078082, "", COMP_CLASS_TO_TEST, "0", "0", 0);
		competition.getCompetitors().add("Fruttberg", 2078040, "", COMP_CLASS_TO_TEST, "0", "0", 0);
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
	
	private List<Card> readCardsFromFiles( String subFolder ) throws Exception{
		SiDriver siDriver = new SiDriver();
		UsbDriverStub stubUsbDriver = new UsbDriverStub();
		siDriver.setUsbDriver(stubUsbDriver);	
		String workingDir = System.getProperty("user.dir");
		File folder = new File(workingDir  + File.separator + "testData" + File.separator + subFolder);
		
		List<Card> cardList = new ArrayList<>();

		for( File fileName : folder.listFiles()){
			if( fileName.getName().contains(".card")){
				List<byte[]> cardRawData = SiDriverTest.readSiacTestDataFromFile(File.separator + subFolder + File.separator + fileName.getName());
				stubUsbDriver.setStubUsbData(cardRawData);
				cardList.add( siDriver.getSiacCardData(false) );
			}
		}	
		
		return cardList;
	}
	
	private void readCompetitorsFromFile( final String subFolder, final String COMP_CLASS_TO_TEST, Competition competition) throws IOException{
		BufferedReader competitorsFileBuffer = null;
		String workingDir = System.getProperty("user.dir");
		try{
			File competitorsFile = new File( workingDir + File.separator + "testData" + File.separator + subFolder + File.separator + "competitors.csv");
			competitorsFileBuffer = new BufferedReader(new FileReader(competitorsFile.getAbsoluteFile()));
			String line;
			while ((line = competitorsFileBuffer.readLine()) != null) {
				String[] parsedLine = line.split(",");			
				assertEquals(2, parsedLine.length);
				String name = parsedLine[0];
				int number = Integer.parseInt( parsedLine[1] );
				competition.getCompetitors().add(name, number , "", COMP_CLASS_TO_TEST, "0", "0", 0);	
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			competitorsFileBuffer.close();
		}	
	}
	
	private int pasrseTime(String time){
		try{
			if(time.contains("DNF") ){
				return 5000000;
			}
			if(time.contains("no result") ){
				return 10000000;
			}
			String[] parsed = time.split(":");
			int minute = Integer.parseInt(parsed[0]);
			int second = Integer.parseInt(parsed[1]);
			return (minute*60)+second;
			}
		catch(NumberFormatException e){
			e.printStackTrace();
			System.out.println("Could not parse: " + time);
		}
		return 60000;
	}
	
	private List<String> readExpectedCompResults() throws IOException{
		List<String> readData = new ArrayList<>();
		BufferedReader expectedDataFileBuffer = null;
		String workingDir = System.getProperty("user.dir");
		try{
			File expectedResultsFile = new File( workingDir + File.separator + "testData" + File.separator + "lackareback_competitionData" + File.separator + "expectedData.csv");
			expectedDataFileBuffer = new BufferedReader(new FileReader(expectedResultsFile.getAbsoluteFile()));
			String line;
			while ((line = expectedDataFileBuffer.readLine()) != null) {
				String[] parsedLine = line.split(",");
				String name = parsedLine[0];
				String number = parsedLine[1];
				int totalTime = pasrseTime( parsedLine[2]);
				int stage1 = pasrseTime( parsedLine[3]);
				int stage2 = pasrseTime( parsedLine[4]);
				int stage3 = pasrseTime( parsedLine[5]);
				int stage4 = pasrseTime( parsedLine[6]);
				int stage5 = pasrseTime( parsedLine[7]);
				int stage6 = pasrseTime( parsedLine[8]);
				readData.add(name+","+number+","+totalTime+","+stage1+","+stage2+","+stage3+","+stage4+","+stage5+","+stage6);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			expectedDataFileBuffer.close();
		}		
		return readData;
	}
	

}
