package se.gsc.stenmark.gscenduro.compmanagement.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;

public class CompetitorTest {

	@Test
	public void test() {
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
		
		Long slowestOnStage1Filter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 1, competition.getResults());
		System.out.println("Slowest on stage 1 with filter " + slowestOnStage1Filter);
		Long slowestOnStage1NoFilter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 1);
		System.out.println("Slowest on stage 1 without filter " + slowestOnStage1NoFilter);
		
		Long slowestOnStage2Filter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 2, competition.getResults());
		System.out.println("Slowest on stage 2 with filter  " + slowestOnStage2Filter);
		Long slowestOnStage2NoFilter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 2);
		System.out.println("Slowest on stage 2 without filter  " + slowestOnStage2NoFilter);
		
		Long slowestOnStage3Filter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 3, competition.getResults());
		System.out.println("Slowest on stage 3 with filter  " + slowestOnStage3Filter);
		Long slowestOnStage3NoFilter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 3);
		System.out.println("Slowest on stage 3 without filter  " + slowestOnStage3NoFilter);
		
		
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
	
		Long slowestOnStage4Filter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 4, competition.getResults());
		System.out.println("Slowest on stage 4 with filter  " + slowestOnStage4Filter);
		Long slowestOnStage4NoFilter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 4);
		System.out.println("Slowest on stage 4 without filter  " + slowestOnStage4NoFilter);
		
		Long slowestOnStage5Filter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 5, competition.getResults());
		System.out.println("Slowest on stage 5 with filter  " + slowestOnStage5Filter);
		Long slowestOnStage5NoFilter = competition.getCompetitors().getSlowestOnStage(COMP_CLASS_TO_TEST, 5);
		System.out.println("Slowest on stage 5 without filter  " + slowestOnStage5NoFilter);
		
		//Stage 1 and Stage 2 just test normal functionality
		assertEquals((Long)135L, slowestOnStage1Filter);
		assertEquals((Long)220L, slowestOnStage1NoFilter);
		assertEquals((Long)90L, slowestOnStage2Filter);
		assertEquals((Long)150L, slowestOnStage2NoFilter);
		
		//Stage3 all competitors within the same time deviation, no competitor shall be filtered out
		assertEquals((Long)190L, slowestOnStage3Filter);
		assertEquals((Long)190L, slowestOnStage3NoFilter);
		
		//Stage4 test that only bottom half of the list it filtered for big time deviations. If the winner is much faster than the rest, we shall not filter the rest out...
		assertEquals((Long)165L, slowestOnStage4Filter);
		assertEquals((Long)295L, slowestOnStage4NoFilter);
		
		//Stage5 set all competitors within a very small timedelta (Seems common in "real" races). Make sure that timedelta cutoff works in filter.
		assertEquals((Long)127L, slowestOnStage5Filter);
		assertEquals((Long)150L, slowestOnStage5NoFilter);
	}

}
