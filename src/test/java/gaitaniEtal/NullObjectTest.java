package gaitaniEtal;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class NullObjectTest extends TestCase {
	
	Refatoracao refatoracao = new Refatoracao();
	NullObject nullObject;
	

	public NullObjectTest() {

	}

	public void testApp() {
		refatoracao.lerProjeto("C:\\programa-java\\exemplo-cavada-gaitani-nullobject");
		assertTrue("Não conseguiu realizar a análise adequada ao padrão", refatoracao.getNullObject().getLeitor().erro == false);
	}
}
