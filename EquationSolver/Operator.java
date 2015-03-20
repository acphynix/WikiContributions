package evolution;

import java.util.Arrays;

public class Operator extends Node{
	public static char[] set=new char[]		{'+','-','*','/','^','s','c','t'};
	public static int[] opargs=new int[]	{-1	, -1, -1, -1, 2 , 1 , 1 , 1 };
	public Operator(char op, Node[] children){
		if(op=='s'||op=='c'||op=='t')c=new Node[1];
		else c=new Node[2];
		this.op=op;
	}
	public char op;
	float parse(float a, float b){
		if(op=='+')return a+b;
		if(op=='-')return a-b;
		if(op=='*')return a*b;
		if(op=='/')return a/b;
		if(op=='^')return (float) Math.pow(a,b);
		if(op=='s')return (float) Math.sin(Math.toRadians(a));
		if(op=='c')return (float) Math.cos(Math.toRadians(a));
		if(op=='t')return (float) Math.tan(Math.toRadians(a));
		return 0;
	}
	public float eval(){
		if(c.length==1){
			if(c[0]==null)return 0;
			return parse(c[0].eval(),0);
		}
		if(this.c[0]==null&&this.c[1]==null)return 0;
		else if(c[0]==null)return c[1].eval();
		else if(c[1]==null)return c[0].eval();
		float a=c[0].eval();
		float b=c[1].eval();
		return parse(a,b);
	}
	protected void cleanself() {
		if(op=='+'){
			if(c[1].equals(c[0])){
				op='*';
				c[1]=Node.constf(2);
			}
			for(int i=0;i<c.length;i++){				// simplify by associativity of children
				if(c[i] instanceof Operator){
					Operator k = (Operator)c[i];
					if(k.op == this.op){
						int oldlen = c.length;
						c=Arrays.copyOf(c, c.length+c[i].c.length);
						c[i]=c[i].c[0];
						for(int j = oldlen; j<c.length;j++){
							c[j]=c[i].c[j-oldlen+1];
						}
					}
				}
			}
		}
		if(op=='*'){
			if(c[1].equals(c[0])){
				op='^';
				c[1]=Node.constf(2);
			}
			for(int i=0;i<c.length;i++){				// simplify by associativity of children
				if(c[i] instanceof Operator){
					Operator k = (Operator)c[i];
					if(k.op == this.op){
						int oldlen = c.length;
						c=Arrays.copyOf(c, c.length+c[i].c.length);
						c[i]=c[i].c[0];
						for(int j = oldlen; j<c.length;j++){
							c[j]=c[i].c[j-oldlen+1];
						}
					}
				}
			}
		}
	}
	public String toString(){
		if(c.length==1){
			return op+"("+(c[0]==null?0:c[0].toString())+")";
		}
		else{
			return "("+(c[0]==null?0:c[0].toString())+" "+op+" "+(c[1]==null?0:c[1].toString())+")";
		}
	}
	public boolean equals(Object o){
		if(o instanceof Operator){
			Operator v=(Operator)o;
			if(c.length!=v.c.length){
				return false;
			}
			for(int i=0;i<c.length;i++){
				if(!c[i].equals(v.c[i])){
					return false;
				}
			}
			return true;
		}return false;
	}
	public static Operator random(){
		Operator n=Node.operator(Operator.set[(int)(Math.random()*(Operator.set.length))]);
		//n.clean();
		return n;
	}
}
