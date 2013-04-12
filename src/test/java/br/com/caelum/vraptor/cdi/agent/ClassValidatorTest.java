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
	
	@Test
	public void shouldAcceptSimpleResource() throws NotFoundException{
		CtClass ctClass = pool.get(SomeController.class.getCanonicalName());
		Assert.assertTrue(validator.isAValid(ctClass));
	}
	
	@Test
	public void shouldAcceptEJBResource() throws NotFoundException{
		CtClass ctClass = pool.get(SomeEJBController.class.getCanonicalName());
		Assert.assertTrue(validator.isAValid(ctClass));
	}
	
	@Test
	public void shouldAcceptInheritedResource() throws NotFoundException{
		CtClass ctClass = pool.get(SomeControllerProxy.class.getCanonicalName());
		Assert.assertTrue(validator.isAValid(ctClass));
	}

}
