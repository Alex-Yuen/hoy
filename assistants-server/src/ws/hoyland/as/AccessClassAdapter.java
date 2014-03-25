package ws.hoyland.as;

import java.io.FileOutputStream;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class AccessClassAdapter extends ClassAdapter {
	public AccessClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	public FieldVisitor visitField(final int access, String name,
			final String desc, final String signature, final Object value) {
		int privateAccess = access;
		// 找到名字为number的变量
		if (name.equals("number"))
			privateAccess = Opcodes.ACC_PUBLIC;
		// 修字段的修饰符为public:在职责链传递过程中替换调用参数
		return cv.visitField(privateAccess, name, desc, signature, value);
	}

	public static void main(String[] args) throws Exception {
		ClassReader cr = new ClassReader("A");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassAdapter classAdapter = new AccessClassAdapter(cw);
		cr.accept(classAdapter, ClassReader.SKIP_DEBUG);
		byte[] data = cw.toByteArray(); // 生成新的字节码文件 File file = new
										// File("A.class");
//		FileOutputStream fout = new FileOutputStream(file);
//		fout.write(data);
//		fout.close();
	}
}