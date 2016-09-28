package se.gsc.stenmark.gscenduro.compmanagement.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Competitors;

public class ImportTest {

	@Test
	public void testImportCompetitors() throws NumberFormatException, IOException {
		System.out.println("Starting ImportCompetitors test");
		
		System.out.println("Test normal SvartVitt correct input");
		Competitors competitorsSvartVitt = new Competitors();
		String errorMessage = competitorsSvartVitt.importCompetitors("Sverker Gustafsson,8633671\nLars Kastensson,8633672\nErik Holmberg,8633673\nFässberg,8633674\nMattias Holmgren,8633675\nHellberg,8633676\nHans Hellsmark,8633677\nPatrik Capretti,8633678\nFredrik Svensson,8633679\nIngemar Gustavsson,8633680\nElin Andreasson,8633681\nAndreas Haag,8633682\nMikael Nordqvist,8633683\nAndreasNäs,8633684\nGerry Bohm,8633685\nAndreas Nilvander,8633686\nJonas Blomster,8633687\nPontus Olofsson,8633688\nPer Johan Andersson,8633689\nMoisés Clemente,8633690\nMark Brannan,8633691\nErik Österberg,8633692", false, Competition.SVART_VIT_TYPE, false);
		verifySvartVit(competitorsSvartVitt, errorMessage);
		
		System.out.println("Test SvartVitt empty lines and double/triple etc. empty lines");
		competitorsSvartVitt = new Competitors();
		errorMessage = competitorsSvartVitt.importCompetitors("Sverker Gustafsson,8633671\n\n\nLars Kastensson,8633672\nErik Holmberg,8633673\n    \nFässberg,8633674\n\n\n\n\nMattias Holmgren,8633675\nHellberg,8633676\nHans Hellsmark,8633677\nPatrik Capretti,8633678\nFredrik Svensson,8633679\nIngemar Gustavsson,8633680\nElin Andreasson,8633681\nAndreas Haag,8633682\nMikael Nordqvist,8633683\nAndreasNäs,8633684\nGerry Bohm,8633685\nAndreas Nilvander,8633686\nJonas Blomster,8633687\nPontus Olofsson,8633688\nPer Johan Andersson,8633689\nMoisés Clemente,8633690\nMark Brannan,8633691\nErik Österberg,8633692\n\n\n\n\n", false, Competition.SVART_VIT_TYPE, false);
		verifySvartVit(competitorsSvartVitt, errorMessage);
		
		System.out.println("Test normal ESS correct input");
		Competitors competitorsEss = new Competitors();
		errorMessage = competitorsEss.importCompetitors("Sverker Gustafsson,8633671,GSC,Motion,1,1\nLars Kastensson,8633672,GSC,Motion,2,grupp1\nErik Holmberg,8633673,GSC,Motion,3,grupp1\nFässberg,8633674,GSC,Motion,4,grupp1\nMattias Holmgren,8633675,GSC,Motion,5,grupp1\nHellberg,8633676,GSC,Motion,6,grupp1\nHans Hellsmark,8633677,GSC,Motion,7,grupp1\nPatrik Capretti,8633678,GSC,Motion,8,grupp1\nFredrik Svensson,8633679,GSC,Motion,9,1\nIngemar Gustavsson,8633680,GSC,Motion,10,1", false, Competition.ESS_TYPE, false);
		verifyEss(competitorsEss, errorMessage);
		
		System.out.println("Test ESS empty lines and double/triple etc. empty lines");
		competitorsEss = new Competitors();
		errorMessage = competitorsEss.importCompetitors("Sverker Gustafsson,8633671,GSC,Motion,1,1\n \n \nLars Kastensson,8633672,GSC,Motion,2,grupp1\n\n\n\nErik Holmberg,8633673,GSC,Motion,3,grupp1\nFässberg,8633674,GSC,Motion,4,grupp1\nMattias Holmgren,8633675,GSC,Motion,5,grupp1\nHellberg,8633676,GSC,Motion,6,grupp1\nHans Hellsmark,8633677,GSC,Motion,7,grupp1\nPatrik Capretti,8633678,GSC,Motion,8,grupp1\nFredrik Svensson,8633679,GSC,Motion,9,1\nIngemar Gustavsson,8633680,GSC,Motion,10,1\n\n\n\n\n", false, Competition.ESS_TYPE, false);
		verifyEss(competitorsEss, errorMessage);

	}
	
	private void verifySvartVit(Competitors competitorsSvartVitt, String errorMessage){
		assertEquals(errorMessage, "");
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
		assertEquals("Per Johan Andersson", competitorsSvartVitt.getByCardNumber(8633689).getName());
		assertEquals("Moisés Clemente", competitorsSvartVitt.getByCardNumber(8633690).getName());
		assertEquals("Mark Brannan", competitorsSvartVitt.getByCardNumber(8633691).getName());
		assertEquals("Erik Österberg", competitorsSvartVitt.getByCardNumber(8633692).getName());
	}
	
	private void verifyEss(Competitors competitorsEss, String errorMessage){
		assertEquals(errorMessage, "");
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
