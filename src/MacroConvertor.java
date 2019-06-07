
public class MacroConvertor {

	public MacroConvertor() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}
	public static void p(Object s) {
		System.out.println(s);
	}
	
	public static void test() {
		String e1 = "#include <stdio.h>\n#expand null NULL\n#expand ADD(a,b, c) a+b+c\n#expand print(ary, n) do {\\\nfor(int i=0;i<n;i++)\\\n{\\\nprintf(\"%d \",ary[i]);\\\n}puts(\"\");\\\n}while(0)\n\nvoid nullify(int ** p)\n{\n\t*p = NULL;\n}\n\nint main(void)\n{\n\tint * a = null;\n\tchar b = ADD(3, 5,7);\n\tchar * null_pointer = null;\n\tnullify(&a);\n\tint ary1[] = {3,5,b};\n\tprint(ary1, 3);\n}";
		
		/*	example testcase
#include <stdio.h>
#expand null NULL
#expand ADD(a,b, c) a+b+c
#expand print(ary, n) do {\
for(int i=0;i<n;i++)\
{\
printf("%d ",ary[i]);\
}puts("");\
}while(0)

void nullify(int ** p)
{
	*p = NULL;
}

int main(void)
{
	int * a = null;
	char b = ADD(3, 5,7);
	char * null_pointer = null;
	nullify(&a);
	int ary1[] = {3,5,b};
	print(ary1, 3);
}
		 */
		p(e1);
		
		
	}

}
