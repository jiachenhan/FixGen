package de.parmol;

import processing.core.*;
import java.util.*;

import de.parmol.graph.Graph;
import de.parmol.parsers.PDGParser;

/**
 * �������ͼ��ͼ�ο��ӻ����
 * ����Processing��ʵ�֡�
 *
 * 
 */
public class GraphView extends PApplet{
	int nodeCount;
	ArrayList<Node> nodes = new ArrayList();
	HashMap<Integer,Node> nodeTable = new HashMap<Integer, Node>();//�����ͽڵ�
	
	int edgeCount;
	ArrayList<Edge> edges = new ArrayList();
	
	Node selection;
	
	//��Ҫ�����ͼ��
	Graph g;
	//���ڿ���PApplet��ѭ��ִ��
	Timer loopTimer = new Timer();
	
	
	static final int nodeColor = 0xFFF0C070;
	static final int selectColor = 0xFFFF3030;
	static final int fixedColor = 0xFFFF8080;
	static final int edgeColor = 0xFF000000;
	
	GraphView(Graph g){
		this.g = g;
	}
	
	/*
	 * ���ӻ�����ڵ���
	 */
	class Node {
		float x, y;
		float dx, dy;
		boolean fixed;
		String label;
		int index;
		
		/**
		 * 
		 * @param index �ڵ��ھ����е�����
		 * @param label �ڵ�ı�ǩ
		 * @param width ��ǰͼ���Ŀ�� ȷ���ʵ��ĵ�����
		 * @param height ��ǰͼ���ĸ߶� ȷ���ʵ��ĵ�����
		 */
		Node(int index, String label) {
			this.index = index;
			this.label = label;
			x = new Random().nextInt(width);
			y = new Random().nextInt(height);
		}
		
		//�����ڵ��ڻ�������ϵ�λ��
		void relax() {
			float ddx = 0;
			float ddy = 0;
			
			for (int j=0; j < nodeCount; j++) {
				Node n = nodes.get(j);
				if (n != this) {
					float vx = x -n.x;
					float vy = y -n.y;
					float lensq = vx*vx+vy*vy;
					if (lensq == 0) {
						ddx += new Random().nextInt(1);
						ddy += new Random().nextInt(1);
					} else if (lensq < 100*100) {
						ddx += vx/lensq;
						ddy += vy/lensq;
					}
				}
			}
			
			float dlen = mag(ddx, ddy) /2;
			if (dlen>0) {
				dx += ddx / dlen;
				dy += ddy / dlen;
			}
			
		}//end of void relax()
		
		//���½ڵ��ڻ�������ϵ�λ��
		void update() {
			if (!fixed) {
				x += constrain(dx, -5, 5);
				y += constrain(dy, -5, 5);
				
				x = constrain(x, 0, width);
				y = constrain(y, 0, height);
			}
			
			dx /=2;
			dy /=2;
		}
		
		//���ƽڵ�ͱ�ǩ
		void draw() {
			if (selection == this) {
				fill(selectColor);
			} else if (fixed) {
				fill(fixedColor);
			} else {
				fill(nodeColor);
			}
			
			stroke(0);
			strokeWeight(0.5f);
			
			ellipseMode(CENTER);
			float r = textWidth(label)+10;
			ellipse(x, y, r, r);
			
			fill(0);
			textAlign(CENTER, CENTER);
			text(label, x, y);
			
		}
		
	}//end of class Node
	
	
	/*
	 * ���ӻ��������
	 */
	class Edge {
		Node from;
		Node to;
		float len;
		String label;
		
		Edge(Node from, Node to, String label) {
			this.from = from;
			this.to = to;
			this.label =label;
			this.len = width/5;
		}
		
		void relax(){
			float vx = to.x-from.x;
			float vy = to.y-from.y;
			float d = mag(vx, vy);
			this.len = width/5;
			if (d>0) {
				float f =(len-d)/(d*3);
				float dx=f*vx;
				float dy =f*vy;
				to.dx += dx;
				to.dy += dy;
				from.dx -= dx;
				from.dy -= dy;
			}
		}//end of void relax()
		
		/*
		 * ���Ʊߡ���ǩ�ͱߵķ��򣨼�ͷ��
		 */
		void draw() {
			stroke(edgeColor);
			strokeWeight(0.35f);
			line(from.x, from.y, to.x, to.y);
			
			fill(0);
			textAlign(CENTER, CENTER);
			text(this.label.toLowerCase(), (from.x+to.x)/2, (from.y+to.y)/2-20);
			
			//���Ʊߵķ����ͷ
			Node end;
			//��С�ߣ���߷���ӱ�ǩС�Ľڵ㵽��Ľڵ�
			if (this.label.compareTo("Z") > 0){
				end= (Integer.parseInt(from.label)-Integer.parseInt(to.label))<0?to:from;
			} else {
				end = (Integer.parseInt(from.label)-Integer.parseInt(to.label))>0?to:from;
			}
			
			float mx=(from.x+to.x)/2;
			float my=(from.y+to.y)/2;
			
			stroke(edgeColor);
			strokeWeight(0.35f);
			
			if (mx<end.x && my<end.y){
				line(mx, my, mx-20, my);
				line(mx, my, mx, my-20);
			}else if (mx>end.x && my<end.y){	
				line(mx, my, mx+20, my);
				line(mx, my, mx, my-20);
			} else if (mx>end.x && my>end.y) {
				line(mx, my, mx+20, my);
				line(mx, my, mx, my+20);
			} else if (mx<end.x && my>end.y) {
				line(mx, my, mx-20, my);
				line(mx, my, mx, my+20);
			}

			
		}//end of void draw()
		
	}//end of class Edge
	
	public void setup() {
		//size(600, 600);
		loadData();
		//noLoop();
	}
	
	public void loadData() {
		//��ӽڵ�
		for (int i = 0; i < g.getNodeCount(); i++) {
			Node n = new Node(i, String.valueOf(g.getNodeLabel(i)) );
			nodes.add(n);
			nodeTable.put(i, n);
		}
		
		//��ӱ�
		for (int i = 0; i < g.getEdgeCount(); i++) {
			final int edge = g.getEdge(i);

			Node from = nodeTable.get( g.getNodeA(edge));
			Node to = nodeTable.get( g.getNodeB(edge));

			Edge e = new Edge(from, to, (char)g.getEdgeLabel(edge)+"");
			edges.add(e);
		}
		
	}//end of loadData
	
	public void draw() {
		background(255);
		
		
		for (Edge e : edges) {
			e.relax();
		}
		for (Node n : nodes) {
			n.relax();
		}
		for(Node n: nodes) {
			n.update();
		}
		
		for(Edge e: edges) {
			e.draw();
		}
		
		for(Node n: nodes) {
			n.draw();
		}
		
		if (selection != null) {
			String s = PDGParser.LabelStatementMap.get(selection.label);
			if (s==null) s = "virtual node";
			fill(0);
			textAlign(CENTER, CENTER);
			text(s, selection.x + textWidth(selection.label) + 0.6f*textWidth(s), selection.y);
		}
		/*if (g.getNodeCount() > 10) {
			loopTimer.schedule(new SetNoLoop(), 15000);
		}*/
	}
	
	
	public void mousePressed(){
		float closest = 20;
		for (Node n:nodes){
			float d = dist(mouseX, mouseY, n.x, n.y);
			if (d<closest){
				selection =n;
				closest=d;
			}
		}
		
		if (selection != null) {
			if(mouseButton == LEFT) {
				selection.fixed = true;
			} else if(mouseButton == RIGHT) {
				selection.fixed = false;
			}
		}
	}
	
	public void mouseDragged(){
		if (selection != null) {
			selection.x = mouseX;
			selection.y = mouseY;
		}
	}
	
	public void mouseRealeased(){
		selection = null;
	}
	
	
	class SetNoLoop extends TimerTask{
		public void run(){
			noLoop();
		}
	}
	
	class SetLoop extends TimerTask{
		public void run(){
			loop();
		}
	}
	
	
}//end of class GraphView
