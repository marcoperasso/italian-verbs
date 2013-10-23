package perassoft.italianverbs.test;

import java.util.ArrayList;

import perassoft.italianverbs.MyApplication;
import perassoft.italianverbs.Verb;
import perassoft.italianverbs.Verbs;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestVerbs extends TestCase {

	public void testVerify() {
		Verbs verbs = MyApplication.getInstance().getVerbs();
		testVerb(verbs, "ESSERE", "Egli è", 2);
		testVerb(verbs, "sentire", "avendo sentito ", 94);
		testVerb(verbs, "sentire", "avendo sentito ", 94);
		testVerb(verbs, "sentire", " senziente ", 91);
	}

	private void testVerb(Verbs verbs, String sVerb, String voice, int i) {
		Verb verb = verbs.get(sVerb);
		if (verb == null)
		{
			Assert.assertTrue(false);
			return;
		}
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(voice);
		Assert.assertTrue("Match del verbo non corretto", verb.verify(i, answers));
	}

}
