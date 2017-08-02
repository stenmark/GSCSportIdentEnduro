package se.gsc.stenmark.gscenduro.compmanagement.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competitors;

public class ImportTest {

	@Test
	public void testImportCompetition() throws IOException{
		System.out.println("Starting testImportCompetition");
		Competition competition = new Competition();
		final String IMPORT_STRING = "[Name]\n" + 
				"Import Comp\n" + 
				"[/Name]\n" + 
				"[Date]\n" + 
				"16-12-01\n" + 
				"[/Date]\n" + 
				"[Type]\n" + 
				"0\n" + 
				"[/Type]\n" + 
				"[Stages]\n" + 
				"71,72,71,72\n" + 
				"[/Stages]\n" + 
				"[Competitors]\n" + 
				"Andreas Haag,8633682\n" + 
				"Andreas Nilvander,8633686\n" + 
				"AndreasNäs,8633684\n" + 
				"Elin Andreasson,8633681\n" + 
				"Erik Holmberg,8633673\n" + 
				"Erik Österberg,8633692\n" + 
				"Fredrik Svensson,8633679\n" + 
				"Fässberg,8633674\n" + 
				"Gerry Bohm,8633685\n" + 
				"Hans Hellsmark,8633677\n" + 
				"Hellberg,8633676\n" + 
				"[/Competitors]\n" + 
				"[Punches]\n" + 
				"8633682,71,38054000,72,38137000,71,39241000,72,39273000\n" +
				"8633686,71,38133000,72,38209000,71,39333000,72,39367000\n" +
				"8633684,71,38120000,72,38200000,71,39257000,72,39291000\n" +
				"8633681,71,38156000,72,38235000,71,39323000,72,39361000\n" +
				"8633673,71,38025000,72,38098000,71,39224000,72,39253000\n" +
				"8633692,71,38099000,72,38179000,71,39293000,72,39328000\n" +
				"8633679,71,38038000,72,38108000,71,39312000,72,39339000\n" +
				"[/Punches]\n";
		
		final String EXPECTED_EXPORT_STRING = "[Name]\n" +
											"Import Comp\n" +
											"[/Name]\n" +
											"[Date]\n" +
											"16-12-01\n" +
											"[/Date]\n" +
											"[Type]\n" +
											"0\n" +
											"[/Type]\n" +
											"[Stages]\n" +
											"71,72,71,72\n" +
											"[/Stages]\n" +
											"[Competitors]\n" +
											"Andreas Haag,8633682\n" +
											"Andreas Nilvander,8633686\n" +
											"AndreasNäs,8633684\n" +
											"Elin Andreasson,8633681\n" +
											"Erik Holmberg,8633673\n" +
											"Erik Österberg,8633692\n" +
											"Fredrik Svensson,8633679\n" +
											"Fässberg,8633674\n" +
											"Gerry Bohm,8633685\n" +
											"Hans Hellsmark,8633677\n" +
											"Hellberg,8633676\n" +
											"[/Competitors]\n" +
											"[Punches]\n" +
											"8633682,71,38054000,72,38137000,71,39241000,72,39273000\n" +
											"8633686,71,38133000,72,38209000,71,39333000,72,39367000\n" +
											"8633684,71,38120000,72,38200000,71,39257000,72,39291000\n" +
											"8633681,71,38156000,72,38235000,71,39323000,72,39361000\n" +
											"8633673,71,38025000,72,38098000,71,39224000,72,39253000\n" +
											"8633692,71,38099000,72,38179000,71,39293000,72,39328000\n" +
											"8633679,71,38038000,72,38108000,71,39312000,72,39339000\n" +
											"[/Punches]\n";

		StringBuilder status = new StringBuilder();
		competition = CompetitionHelper.importCompetition(IMPORT_STRING, status);
		assertEquals("", status.toString());
		competition.calculateResults();
		
		String competitionAsString = CompetitionHelper.getCompetitionAsString(competition);
		System.out.println("Competition as string\n" + competitionAsString);
		assertEquals(EXPECTED_EXPORT_STRING, competitionAsString);
		
		status = new StringBuilder();
		competition = CompetitionHelper.importCompetition(competitionAsString, status);
		assertEquals("", status.toString());
		competition.calculateResults();
		
		competitionAsString = CompetitionHelper.getCompetitionAsString(competition);
		System.out.println("Competition as string\n" + competitionAsString);
		assertEquals(EXPECTED_EXPORT_STRING, competitionAsString);

	}
	
	@Test 
	public void testImportPunches() throws NumberFormatException, IOException{
		//CardNumber,Control,Time,Control,Time
		System.out.println("Starting importPunches test");
		Competition competition = new Competition();
		competition.importStages("71,72,71,72");
		String errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,8633671\nLars Kastensson,8633672\nErik Holmberg,8633673\nFässberg,8633674\nMattias Holmgren,8633675\nHellberg,8633676\nHans Hellsmark,8633677\nPatrik Capretti,8633678\nFredrik Svensson,8633679\nIngemar Gustavsson,8633680\nElin Andreasson,8633681\nAndreas Haag,8633682\nMikael Nordqvist,8633683\nAndreasNäs,8633684\nGerry Bohm,8633685\nAndreas Nilvander,8633686\nJonas Blomster,8633687\nPontus Olofsson,8633688\nPer Johan Andersson,8633689\nMoisés Clemente,8633690\nMark Brannan,8633691\nErik Österberg,8633692", 
																   false, Competition.SVART_VIT_TYPE, false,competition);
		verifySvartVit(competition.getCompetitors(), errorMessage,22);
		
		String importPunchesStatus = CompetitionHelper.importPunches("8633671,71,100000,72,200000,71,300000,72,490000\n"+
																	 "8633672,71,110000,72,220000,71,300000,72,470000\n"+
																     "8633673,71,120000,72,240000,71,300000,72,410000\n"+
																     "8633674,71,130000,72,260000,71,300000,72,420000\n"+
																	 "8633675,71,140000,72,290000,71,300000,72,430000\n"+
																     "8633676,71,150000,72,320000,71,330000,72,430000\n", 
																	 competition);
		System.out.println("Status: " + importPunchesStatus);
		final String EXPECTED_STATUS = "8633671,71,100000,72,200000,71,300000,72,490000. Added\n"+
										"8633672,71,110000,72,220000,71,300000,72,470000. Added\n"+
										"8633673,71,120000,72,240000,71,300000,72,410000. Added\n"+
										"8633674,71,130000,72,260000,71,300000,72,420000. Added\n"+
										"8633675,71,140000,72,290000,71,300000,72,430000. Added\n"+
										"8633676,71,150000,72,320000,71,330000,72,430000. Added\n"+
										"Processing cards:\n"+
										"Added card: 8633671  Competitor:  Sverker Gustafsson\n"+
										"Added card: 8633672  Competitor:  Lars Kastensson\n"+
										"Added card: 8633673  Competitor:  Erik Holmberg\n"+
										"Added card: 8633674  Competitor:  Fässberg\n"+
										"Added card: 8633675  Competitor:  Mattias Holmgren\n"+
										"Added card: 8633676  Competitor:  Hellberg\n";
		assertEquals(EXPECTED_STATUS, importPunchesStatus);
		competition.calculateResults();
		
		String resultsAsCsvString = CompetitionHelper.getResultsAsCsvString(competition.getStagesForAllClasses(), 
												competition.getTotalResultsForAllClasses(),
												competition.getCompetitors(),
												Competition.SVART_VIT_TYPE);
		
		System.out.println(resultsAsCsvString);
		final String EXPECTED_RESULT =  "Rank,Name,Card Number,Total Time,Stage 1,Rank,Time Back,Stage 2,Rank,Time Back,\n"+
										"1,Erik Holmberg,8633673,03:50.0,02:00.0,3,00:20.0,01:50.0,2,00:10.0,\n"+
										"2,Fässberg,8633674,04:10.0,02:10.0,4,00:30.0,02:00.0,3,00:20.0,\n"+
										"3,Hellberg,8633676,04:30.0,02:50.0,6,01:10.0,01:40.0,1,00:00.0,\n"+
										"4,Lars Kastensson,8633672,04:40.0,01:50.0,2,00:10.0,02:50.0,5,01:10.0,\n"+
										"4,Mattias Holmgren,8633675,04:40.0,02:30.0,5,00:50.0,02:10.0,4,00:30.0,\n"+
										"6,Sverker Gustafsson,8633671,04:50.0,01:40.0,1,00:00.0,03:10.0,6,01:30.0,\n"+
										"-,Andreas Haag,8633682,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Andreas Nilvander,8633686,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,AndreasNäs,8633684,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Elin Andreasson,8633681,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Erik Österberg,8633692,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Fredrik Svensson,8633679,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Gerry Bohm,8633685,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Hans Hellsmark,8633677,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Ingemar Gustavsson,8633680,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Jonas Blomster,8633687,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Mark Brannan,8633691,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Mikael Nordqvist,8633683,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Moisés Clemente,8633690,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Patrik Capretti,8633678,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Per Johan Andersson,8633689,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n"+
										"-,Pontus Olofsson,8633688,card not read,card not read,30000000,card not read,card not read,30000000,card not read,\n";
		assertEquals(EXPECTED_RESULT, resultsAsCsvString);
		
		String exportPunchesCsvString = competition.getCompetitors().exportPunchesCsvString();
		System.out.println(exportPunchesCsvString);
		final String EXPECTED_EXPORT_PUNCHES = "8633673,71,120000,72,240000,71,300000,72,410000\n"+
												"8633674,71,130000,72,260000,71,300000,72,420000\n"+
												"8633676,71,150000,72,320000,71,330000,72,430000\n"+
												"8633672,71,110000,72,220000,71,300000,72,470000\n"+
												"8633675,71,140000,72,290000,71,300000,72,430000\n"+
												"8633671,71,100000,72,200000,71,300000,72,490000\n";
		assertEquals(EXPECTED_EXPORT_PUNCHES, exportPunchesCsvString);
		
		importPunchesStatus = CompetitionHelper.importPunches(exportPunchesCsvString, competition);
		System.out.println(importPunchesStatus);
		final String EXPECTED_STATUS_REIMPORT = "8633673,71,120000,72,240000,71,300000,72,410000. Added\n"+
												"8633674,71,130000,72,260000,71,300000,72,420000. Added\n"+
												"8633676,71,150000,72,320000,71,330000,72,430000. Added\n"+
												"8633672,71,110000,72,220000,71,300000,72,470000. Added\n"+
												"8633675,71,140000,72,290000,71,300000,72,430000. Added\n"+
												"8633671,71,100000,72,200000,71,300000,72,490000. Added\n"+
												"Processing cards:\n"+
												"Added card: 8633673  Competitor:  Erik Holmberg\n"+
												"Added card: 8633674  Competitor:  Fässberg\n"+
												"Added card: 8633676  Competitor:  Hellberg\n"+
												"Added card: 8633672  Competitor:  Lars Kastensson\n"+
												"Added card: 8633675  Competitor:  Mattias Holmgren\n"+
												"Added card: 8633671  Competitor:  Sverker Gustafsson\n";
												assertEquals(EXPECTED_STATUS_REIMPORT, importPunchesStatus);
		competition.calculateResults();
		resultsAsCsvString = CompetitionHelper.getResultsAsCsvString(competition.getStagesForAllClasses(), 
							 competition.getTotalResultsForAllClasses(),
							 competition.getCompetitors(),
							 Competition.SVART_VIT_TYPE);
		assertEquals(EXPECTED_RESULT, resultsAsCsvString);
	}
	
	@Test
	public void testImportCompetitors() throws NumberFormatException, IOException {
		System.out.println("Starting ImportCompetitors test");
		
		System.out.println("Test normal SvartVitt correct input");
		Competition competition = new Competition();
		String errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,8633671\nLars Kastensson,8633672\nErik Holmberg,8633673\nFässberg,8633674\nMattias Holmgren,8633675\nHellberg,8633676\nHans Hellsmark,8633677\nPatrik Capretti,8633678\nFredrik Svensson,8633679\nIngemar Gustavsson,8633680\nElin Andreasson,8633681\nAndreas Haag,8633682\nMikael Nordqvist,8633683\nAndreasNäs,8633684\nGerry Bohm,8633685\nAndreas Nilvander,8633686\nJonas Blomster,8633687\nPontus Olofsson,8633688\nPer Johan Andersson,8633689\nMoisés Clemente,8633690\nMark Brannan,8633691\nErik Österberg,8633692", false, Competition.SVART_VIT_TYPE, false,competition);
		verifySvartVit(competition.getCompetitors(), errorMessage,22);

		System.out.println("Test SvartVitt empty lines and double/triple etc. empty lines");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,8633671\n\n\nLars Kastensson,8633672\nErik Holmberg,8633673\n    \nFässberg,8633674\n\n\n\n\nMattias Holmgren,8633675\nHellberg,8633676\nHans Hellsmark,8633677\nPatrik Capretti,8633678\nFredrik Svensson,8633679\nIngemar Gustavsson,8633680\nElin Andreasson,8633681\nAndreas Haag,8633682\nMikael Nordqvist,8633683\nAndreasNäs,8633684\nGerry Bohm,8633685\nAndreas Nilvander,8633686\nJonas Blomster,8633687\nPontus Olofsson,8633688\nPer Johan Andersson,8633689\nMoisés Clemente,8633690\nMark Brannan,8633691\nErik Österberg,8633692\n\n\n\n\n", false, Competition.SVART_VIT_TYPE, false,competition);
		verifySvartVit(competition.getCompetitors(), errorMessage,22);
		
		System.out.println("Test SvartVitt import with semicolon instead of colon");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson;8633671\nLars Kastensson;8633672\nErik Holmberg;8633673\nFässberg;8633674\nMattias Holmgren;8633675\nHellberg;8633676\nHans Hellsmark;8633677\nPatrik Capretti;8633678\nFredrik Svensson;8633679\nIngemar Gustavsson;8633680\nElin Andreasson;8633681\nAndreas Haag;8633682\nMikael Nordqvist;8633683\nAndreasNäs;8633684\nGerry Bohm;8633685\nAndreas Nilvander;8633686\nJonas Blomster;8633687\nPontus Olofsson;8633688\nPer Johan Andersson;8633689\nMoisés Clemente;8633690\nMark Brannan;8633691\nErik Österberg;8633692", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		verifySvartVit(competition.getCompetitors(), errorMessage,22);
		
		System.out.println("Test SvartVitt import with just name (no card)");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		verifySvartVit(competition.getCompetitors(), errorMessage,0);
		
		System.out.println("Test SvartVitt import with just card (no name)");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors(",1234", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		verifySvartVit(competition.getCompetitors(), errorMessage,0);
		
		System.out.println("Test SvartVitt import same cardnumber twice");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,8633671\nLars Kastensson,8633672\nErik Holmberg,8633673\nFässberg,8633674\nMattias Holmgren,8633675\nHellberg,8633676\nHans Hellsmark,8633677\nPatrik Capretti,8633678\nFredrik Svensson,8633679\nIngemar Gustavsson,8633680\nElin Andreasson,8633681\nAndreas Haag,8633682\nMikael Nordqvist,8633683\nAndreasNäs,8633684\nGerry Bohm,8633685\nAndreas Nilvander,8633686\nJonas Blomster,8633687\nPontus Olofsson,8633688\nPer Johan Andersson,8633671\nMoisés Clemente,8633690\nMark Brannan,8633691\nErik Österberg,8633692", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		verifySvartVit(competition.getCompetitors(), errorMessage,21);
		
		System.out.println("Test SvartVitt import with same name twice");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker,1234\nSverker,45667", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		assertEquals("Sverker", competition.getCompetitors().getByCardNumber(1234).getName() );
		assertEquals("Sverker", competition.getCompetitors().getByCardNumber(45667).getName() );
		assertEquals(2, competition.getCompetitors().size() );
		
		System.out.println("Test SvartVitt import with incorrect cardnumber");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,asdfg", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		verifySvartVit(competition.getCompetitors(), errorMessage,0);
		
		System.out.println("Test SvartVitt import only dam klass");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverkina Gustafsson,1234,dam\nJosefin,567,dam", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		verifySvartVitDam(competition.getCompetitors(), errorMessage,2, competition);
		
		System.out.println("Test SvartVitt import both normal and dam klass");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,8633671\nLars Kastensson,8633672\nJosefin,567,dam\nErik Holmberg,8633673\nHelena,1234,dam", false, Competition.SVART_VIT_TYPE, false,competition);
		System.out.println(errorMessage);
		verifySvartVitDamHerrMixed(competition.getCompetitors(), errorMessage,5, competition);
		
		System.out.println("Test normal ESS correct input");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,8633671,GSC,Motion,1,1\nLars Kastensson,8633672,GSC,Motion,2,grupp1\nErik Holmberg,8633673,GSC,Motion,3,grupp1\nFässberg,8633674,GSC,Motion,4,grupp1\nMattias Holmgren,8633675,GSC,Motion,5,grupp1\nHellberg,8633676,GSC,Motion,6,grupp1\nHans Hellsmark,8633677,GSC,Motion,7,grupp1\nPatrik Capretti,8633678,GSC,Motion,8,grupp1\nFredrik Svensson,8633679,GSC,Motion,9,1\nIngemar Gustavsson,8633680,GSC,Motion,10,1", false, Competition.ESS_TYPE, false,competition);
		verifyEss(competition.getCompetitors(), errorMessage);
		
		System.out.println("Test ESS empty lines and double/triple etc. empty lines");
		competition = new Competition();
		errorMessage = CompetitionHelper.importCompetitors("Sverker Gustafsson,8633671,GSC,Motion,1,1\n \n \nLars Kastensson,8633672,GSC,Motion,2,grupp1\n\n\n\nErik Holmberg,8633673,GSC,Motion,3,grupp1\nFässberg,8633674,GSC,Motion,4,grupp1\nMattias Holmgren,8633675,GSC,Motion,5,grupp1\nHellberg,8633676,GSC,Motion,6,grupp1\nHans Hellsmark,8633677,GSC,Motion,7,grupp1\nPatrik Capretti,8633678,GSC,Motion,8,grupp1\nFredrik Svensson,8633679,GSC,Motion,9,1\nIngemar Gustavsson,8633680,GSC,Motion,10,1\n\n\n\n\n", false, Competition.ESS_TYPE, false,competition);
		verifyEss(competition.getCompetitors(), errorMessage);

	}
	
	private void verifySvartVitDam(Competitors competitorsSvartVitt, String errorMessage, int numberOfCompetitors, Competition competition){ 
		assertEquals(numberOfCompetitors, competitorsSvartVitt.size());
		if(numberOfCompetitors > 0){
			assertEquals("Sverkina Gustafsson", competitorsSvartVitt.getByCardNumber(1234).getName());
			assertEquals("dam", competitorsSvartVitt.getByCardNumber(1234).getCompetitorClass());
			assertEquals("Josefin", competitorsSvartVitt.getByCardNumber(567).getName());
			assertEquals("dam", competitorsSvartVitt.getByCardNumber(567).getCompetitorClass());
			assertEquals(1, competition.getAllClasses().size() );
			assertNotNull(competition.getTotalResults("dam") );
		}
	}
	
	private void verifySvartVitDamHerrMixed(Competitors competitorsSvartVitt, String errorMessage, int numberOfCompetitors, Competition competition){ 
		assertEquals(numberOfCompetitors, competitorsSvartVitt.size());
		if(numberOfCompetitors > 0){
			assertEquals("Sverker Gustafsson", competitorsSvartVitt.getByCardNumber(8633671).getName());
			assertEquals("", competitorsSvartVitt.getByCardNumber(8633671).getCompetitorClass());
			assertEquals("Lars Kastensson", competitorsSvartVitt.getByCardNumber(8633672).getName());
			assertEquals("", competitorsSvartVitt.getByCardNumber(8633672).getCompetitorClass());
			assertEquals("Josefin", competitorsSvartVitt.getByCardNumber(567).getName());
			assertEquals("dam", competitorsSvartVitt.getByCardNumber(567).getCompetitorClass());
			assertEquals("Erik Holmberg", competitorsSvartVitt.getByCardNumber(8633673).getName());
			assertEquals("", competitorsSvartVitt.getByCardNumber(8633673).getCompetitorClass());
			assertEquals("Helena", competitorsSvartVitt.getByCardNumber(1234).getName());
			assertEquals("dam", competitorsSvartVitt.getByCardNumber(1234).getCompetitorClass());
			assertEquals(2, competition.getAllClasses().size() );
			assertNotNull(competition.getTotalResults("dam") );
			assertNotNull(competition.getTotalResults("") );
		}
	}
	
	private void verifySvartVit(Competitors competitorsSvartVitt, String errorMessage, int numberOfCompetitors){
		assertEquals(numberOfCompetitors, competitorsSvartVitt.size());
		if(numberOfCompetitors > 0){
			if( numberOfCompetitors != 21){
				assertEquals(errorMessage, "");
			}
			assertEquals("Sverker Gustafsson", competitorsSvartVitt.getByCardNumber(8633671).getName());
			assertEquals("Lars Kastensson", competitorsSvartVitt.getByCardNumber(8633672).getName());
			assertEquals("Erik Holmberg", competitorsSvartVitt.getByCardNumber(8633673).getName());
			assertEquals("Fässberg", competitorsSvartVitt.getByCardNumber(8633674).getName());
			assertEquals("Mattias Holmgren", competitorsSvartVitt.getByCardNumber(8633675).getName());
			assertEquals("Hellberg", competitorsSvartVitt.getByCardNumber(8633676).getName());
			assertEquals("Hans Hellsmark", competitorsSvartVitt.getByCardNumber(8633677).getName());
			assertEquals("Patrik Capretti", competitorsSvartVitt.getByCardNumber(8633678).getName());
			assertEquals("Fredrik Svensson", competitorsSvartVitt.getByCardNumber(8633679).getName());
			assertEquals("Ingemar Gustavsson", competitorsSvartVitt.getByCardNumber(8633680).getName());
			assertEquals("Elin Andreasson", competitorsSvartVitt.getByCardNumber(8633681).getName());
			assertEquals("Andreas Haag", competitorsSvartVitt.getByCardNumber(8633682).getName());
			assertEquals("Mikael Nordqvist", competitorsSvartVitt.getByCardNumber(8633683).getName());
			assertEquals("AndreasNäs", competitorsSvartVitt.getByCardNumber(8633684).getName());
			assertEquals("Gerry Bohm", competitorsSvartVitt.getByCardNumber(8633685).getName());
			assertEquals("Andreas Nilvander", competitorsSvartVitt.getByCardNumber(8633686).getName());
			assertEquals("Jonas Blomster", competitorsSvartVitt.getByCardNumber(8633687).getName());
			assertEquals("Pontus Olofsson", competitorsSvartVitt.getByCardNumber(8633688).getName());
			if( numberOfCompetitors != 21){
				assertEquals("Per Johan Andersson", competitorsSvartVitt.getByCardNumber(8633689).getName());
			}
			assertEquals("Moisés Clemente", competitorsSvartVitt.getByCardNumber(8633690).getName());
			assertEquals("Mark Brannan", competitorsSvartVitt.getByCardNumber(8633691).getName());
			assertEquals("Erik Österberg", competitorsSvartVitt.getByCardNumber(8633692).getName());
		}
		else{
			assertFalse("Expected error message to contain something. Message was: " + errorMessage, errorMessage.isEmpty());
		}
	}
	
	private void verifyEss(Competitors competitorsEss, String errorMessage){
		assertEquals(errorMessage, "");
		assertEquals(10, competitorsEss.size());
		assertEquals("Sverker Gustafsson", competitorsEss.getByCardNumber(8633671).getName());
		assertEquals("Lars Kastensson", competitorsEss.getByCardNumber(8633672).getName());
		assertEquals("Erik Holmberg", competitorsEss.getByCardNumber(8633673).getName());
		assertEquals("Fässberg", competitorsEss.getByCardNumber(8633674).getName());
		assertEquals("Mattias Holmgren", competitorsEss.getByCardNumber(8633675).getName());
		assertEquals("Hellberg", competitorsEss.getByCardNumber(8633676).getName());
		assertEquals("Hans Hellsmark", competitorsEss.getByCardNumber(8633677).getName());
		assertEquals("Patrik Capretti", competitorsEss.getByCardNumber(8633678).getName());
		assertEquals("Fredrik Svensson", competitorsEss.getByCardNumber(8633679).getName());
		assertEquals("Ingemar Gustavsson", competitorsEss.getByCardNumber(8633680).getName());
	}

}
