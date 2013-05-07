package br.com.caelum.vraptor.cdi.agent;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javassist.NotFoundException;

import org.junit.Test;

public class ConstructorAdapterTest {

	@Test
	public void shouldBeOkInstantiateANewObject() throws NotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		SomeController controller = SomeController.class.getConstructor().newInstance();		
		System.out.println(controller+"===>");
		//System.out.println(Arrays.toString(SomeController.class.getConstructor(String.class).getAnnotations()));
	}
}
