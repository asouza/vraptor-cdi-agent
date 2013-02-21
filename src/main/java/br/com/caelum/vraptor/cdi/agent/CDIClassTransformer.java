package br.com.caelum.vraptor.cdi.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;

public class CDIClassTransformer implements ClassFileTransformer {
	
	private ClassPool pool = ClassPool.getDefault();

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			className = className.replace('/', '.');
			pool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
			CtClass modified = new ConstructorAdapter(pool)
			.tryToAddCDIConstructorFor(className);			
			return modified.toBytecode();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new CDIClassTransformer());

	}

}
