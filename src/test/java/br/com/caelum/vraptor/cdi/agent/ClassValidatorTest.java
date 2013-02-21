package br.com.caelum.vraptor.cdi.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.junit.Assert;
import org.junit.Test;


public class ClassValidatorTest {
	
	private final ClassPool pool = ClassPool.getDefault();
	private final ClassValidator validator = new ClassValidator();

	@Test
	public void simpleInterfaceShouldBeNotValid() throws NotFoundException{
		CtClass ctClass = pool.get(SimpleInterface.class.getCanonicalName());
		Assert.assertFalse(validator.isAValid(ctClass));
	}
	@Test
	public void annotatedInterfaceShouldBeNotValid() throws NotFoundException{
		CtClass ctClass = pool.get(InterfaceWithValidAnnotation.class.getCanonicalName());
		Assert.assertFalse(validator.isAValid(ctClass));
	}

}
