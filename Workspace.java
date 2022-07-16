import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;

public class Workspace extends JFrame implements MouseListener, ActionListener
{	
	// Se presupune un maxim de 100 noduri & muchii.
	// Necesită implementarea stocării prin alocare dinamică
	// a memoriei pentru a asigura funcționalitatea
	// cu orice număr de elemente.
	Node[] nList = new Node[100];
	Road[] lList = new Road[100];
	public static int k=0, m=0; // k - noduri; m - muchii.
	public static int linkFirst=-1; // Nodul de la care pleacă o muchie.
	
	
	// Butoanele de schimbare a acțiunii click-ului.
	JPanel controls = new JPanel();
	JButton createNode = new JButton();
	JButton eraseNode = new JButton();
	JButton createLink = new JButton();
	JButton eraseLink = new JButton();
	JButton clearAll = new JButton();
	JButton path = new JButton();
	public static char act = 's';
	
	// Instrucțiunile
	JPanel title = new JPanel();
	JLabel titleText = new JLabel();
	
	Workspace()
	{
		// Spațiul de lucru
		setTitle("Dots");
		setSize(720,720);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.ORANGE);
		
		// Text
		titleText.setForeground(Color.PINK);
		titleText.setFont(new Font("Times New Roman", Font.BOLD, 40));
		titleText.setText("Selectați acțiunea");
		titleText.setHorizontalAlignment(JLabel.CENTER);
		title.setLayout(new BorderLayout());
		title.setBackground(Color.BLACK);
		title.setPreferredSize(new Dimension(720,50));
		title.add(titleText);
		
		// Butoane
		createNode.setText("Adaugă noduri");
		createNode.setPreferredSize(new Dimension(360,50));
		createNode.setFocusable(false);
		createNode.addActionListener(this);
		eraseNode.setText("Șterge noduri");
		eraseNode.setPreferredSize(new Dimension(360,50));
		eraseNode.setFocusable(false);
		eraseNode.addActionListener(this);
		createLink.setText("Adaugă muchie");
		createLink.setPreferredSize(new Dimension(360,50));
		createLink.setFocusable(false);
		createLink.addActionListener(this);
		eraseLink.setText("Șterge muchie");
		eraseLink.setPreferredSize(new Dimension(360,50));
		eraseLink.setFocusable(false);
		eraseLink.addActionListener(this);
		clearAll.setText("Șterge tot");
		clearAll.setPreferredSize(new Dimension(360,50));
		clearAll.setFocusable(false);
		clearAll.addActionListener(this);
		path.setText("Găsește drum");
		path.setPreferredSize(new Dimension(360,50));
		path.setFocusable(false);
		path.addActionListener(this);

		controls.setLayout(new GridLayout(2,3));
		controls.setVisible(true);
		controls.add(createNode);
		controls.add(createLink);
		controls.add(path);
		controls.add(eraseNode);
		controls.add(eraseLink);
		controls.add(clearAll);
		
		add(title, BorderLayout.NORTH);
		add(controls, BorderLayout.SOUTH);
		addMouseListener(this);
		setVisible(true);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		int x=e.getX(), y=e.getY();
		switch(act)
		{
		case 'a':
			addNode(x,y);
			break;
		case 'e':
			delNode(x,y);
			break;
		case 'l':
			addLink(x,y);
			break;
		case 'd':
			delLink(x,y);
			break;
		case 'p':
			pathFindTrigger(x,y);
		}
	}
	
	// Verifică dacă noul cerc adăugat nu s-ar suprapune
	// cu un cerc deja existent, calculând distanța dintre
	// centrele celor două cercuri.
	// Returnează numărul cercului cu care s-ar suprapune,
	// sau -1 în caz contrar.
	public int overlapping(int a, int b)
	{
		for(int i=0; i<k; i++)
			if(Math.pow(a-nList[i].x,2)+Math.pow(b-nList[i].y,2)<36*36)
				return i;
		return -1;
	}
	
	// Verifică dacă mouse-ul se află plasat pe un nod, adică 
	// dacă distanța dintre mouse și centrul cercului <= raza.
	// Returnează numărul nodului, sau -1 în caz contrar.
	public int mouseOver(int a, int b)
	{
		for(int i=0; i<k; i++)
			if(Math.pow(a-nList[i].x,2)+Math.pow(b-nList[i].y,2)<18*18)
				return i;
		return -1;
	}
	
	public void addNode(int x, int y)
	{
		if(y<100 || y>590) return; // Nodurile să nu intre în text/butoane.
		int over=overlapping(x,y); // Nodurile să nu se suprapună.
		if(over==-1)
			{ nList[k++]= new Node(x,y,getGraphics()); }
		else
			JOptionPane.showMessageDialog(null, "Alege altă locație.", "Suprapunere!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void delNode(int x, int y)
	{
		int over=mouseOver(x,y);
		if(over!=-1)
		{
			for(int i=0; i<m; i++) // Asigură ștergerea muchiilor adiacente nodului șters.
				if(lList[i].start.equals(nList[over]) ||
				   lList[i].end.equals(nList[over]))
				{
						lList[i].delete(getGraphics());
						for(int j=i; j<m; j++)
							lList[j]=lList[j+1];
						m--;
						i--;
				}
			nList[over].delete(getGraphics()); // Șterge nodul propriu-zis.
			for(int i=over; i<k; i++)
				nList[i]=nList[i+1];
			k--;
		}
	}
	
	public void addLink(int x, int y)
	{
		int over=mouseOver(x,y);
		if(over!=-1)
			if(linkFirst==-1) // Memorează primul capăt al muchiei.
			{
				linkFirst=over;
				titleText.setText("[Adăugare] Alege al doilea nod");
			}
			else if(over!=linkFirst)
			{
				boolean duplicate = false; // Verifică să nu se repete o muchie deja existentă.
				for(int i=0; i<m; i++)
						if(lList[i].start.equals(nList[linkFirst]) && lList[i].end.equals(nList[over]) ||
						   lList[i].start.equals(nList[over]) && lList[i].end.equals(nList[linkFirst]))
							duplicate = true;
				if(!duplicate)
					lList[m++] = new Road(nList[linkFirst],nList[over],getGraphics(), Color.BLUE);
				else
					JOptionPane.showMessageDialog(null, "Alege alte noduri", "Muchie deja existentă!", JOptionPane.INFORMATION_MESSAGE);
				titleText.setText("Selectați acțiunea");
				linkFirst=-1;
				act='s';
			}
	}
	
	public void delLink(int x, int y)
	{
		int over=mouseOver(x,y);
		if(over!=-1)
			if(linkFirst==-1)
			{
				linkFirst=over;
				titleText.setText("[Ștergere] Alege al doilea nod");
			}
			else if(over!=linkFirst)
			{
				for(int i=0; i<m; i++)
					if(lList[i].start.equals(nList[linkFirst]) && lList[i].end.equals(nList[over]) ||
					   lList[i].start.equals(nList[over]) && lList[i].end.equals(nList[linkFirst]))
					{
						lList[i].delete(getGraphics());
						for(int j=i; j<m; j++)
							lList[j]=lList[j+1];
						m--;
					}
				titleText.setText("Selectați acțiunea");
				linkFirst = -1;
				act = 's';
			}
	}
	
	// Șterge toate nodurile & muchiile.
	// Trimite „NullPointerException”, chiar dacă 
	// programul funcționează corect.
	public void eraseAll()
	{
		try
		{
			while(k>=0)
				delNode(nList[0].x,nList[0].y);
			k=0; m=0;
			act='s';
			titleText.setText("Selectați acțiunea");
		}
		catch (Exception e) {}
	}
	
	// Memorează cele două noduri între care se caută drumul
	// apoi apelează funcția de căutare propriu-zisă.
	public void pathFindTrigger(int a, int b)
	{
		int over=mouseOver(a,b);
		if(over!=-1)
			if(linkFirst==-1)
			{
				linkFirst=over;
				titleText.setText("[Drum] Alege al doilea nod");
			}
			else if(over!=linkFirst)
				 {
					pathFind(linkFirst, over);
					linkFirst = -1;
				 }
	}

	// Găsește un drum (ORICARE!) dintre două noduri, dacă există.
	// DFS, complexitate O(n^2)...
	// !! Funcția găsește drumuri în ordinea nodurilor.
	public void pathFind(int s, int e)
	{
		int[] answer = new int[100];
		int r = 0;
		boolean[] passed = new boolean[100];
		boolean found = false;
		answer[0] = s; passed[s] = true;
		while(r>=0 && !found)
		{
			if(answer[r]==e)
			{
				found = true;
				break;
			}
			for(int i=0; i<k; i++) // Parcurge toate nodurile
			{					   // care n-au fost vizitate deja.
				if(!passed[i])     
					for(int j=0; j<m; j++)
					{
						if(lList[j].start.equals(nList[answer[r]]) && lList[j].end.equals(nList[i])) // Caută muchii care încep cu ultimul nod vizitat.
						{
							answer[++r] = i;
							r++;
							passed[i] = true;
							i=k;
							break;
						}
						if(lList[j].end.equals(nList[answer[r]]) && lList[j].start.equals(nList[i])) // Caută muchii care se termină cu ultimul nod vizitat.
						{
							answer[++r] = i;
							r++;
							passed[i] = true;
							i=k;
							break;
						}
					}
			}
			r--;
		}
		if(r==-1) titleText.setText("Nu există drum între nodurile selectate!");
		else
		{
			titleText.setText("Unul dintre drumurile posibile");
			for(int i=0; i<r; i++)
			{
				for(int j=0; j<m; j++)
					if(lList[j].start.equals(nList[answer[i]]) && lList[j].end.equals(nList[answer[i+1]]) ||
					   lList[j].end.equals(nList[answer[i]]) && lList[j].start.equals(nList[answer[i+1]]))
					   {
							lList[j].paint(getGraphics(), new Color(50,50*i,50));
							try { Thread.sleep(1000); } 
							catch (InterruptedException e1)
							{ e1.printStackTrace(); }
							break;
					   }
			}
		}
	}
	
	// Schimbă acțiunea click-ului.
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==createNode)
		{
			act='a';
			titleText.setText("Adăugare noduri");
		}
		if(e.getSource()==eraseNode)
		{
			act='e';
			titleText.setText("Ștergere noduri");
		}
		if(e.getSource()==createLink)
		{
			act='l';
			titleText.setText("[Adăugare] Alege primul nod");
		}
		if(e.getSource()==eraseLink)
		{
			act='d';
			titleText.setText("[Ștergere] Alege primul nod");
		}
		if(e.getSource()==clearAll)
		{
			act='s';
			eraseAll();
		}
		if(e.getSource()==path)
		{
			act='p';
			titleText.setText("[Drum] Alege primul nod");
		}
	}
	
	// Funcții reziduale de la MouseListener
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

}
