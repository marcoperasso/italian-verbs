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
		Verb verb = verbs.get("ESSERE");
		if (verb == null)
		{
			Assert.assertTrue(false);
			return;
		}
		ArrayList<String> answers = new ArrayList<String>();
		answers.add("Egli è");
		Assert.assertTrue("Match del verbo non corretto", verb.verify(2, answers));
	}

}
