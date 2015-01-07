
package guessGame.frontend;

import guessGame.ImageTask;
import guessGame.Task;
import guessGame.frontend.AnswerPanel;
import guessGame.paint.message.ClearMessage;
import guessGame.paint.message.PaintMessage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;


public class Client extends JFrame {

	private static final long serialVersionUID = -6463718980738496419L;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private TaskPanel taskPanel;
	private AnswerPanel lowerPanel;
	private JButton nextButton;
	private HttpClient client;
	private TaskPanelFactory taskPanelFactory;

	public Client() throws Exception {

		this.setTitle("Client Game");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setSize(800, 600);

		this.taskPanel = new TaskPanel();
		this.taskPanelFactory = new TaskPanelFactory();
		this.taskPanel.setPreferredSize(new Dimension(600, 450));
		this.add(taskPanel, BorderLayout.NORTH);
		
		this.lowerPanel = new AnswerPanel();
		this.nextButton = new JButton("Next");
		this.nextButton.addActionListener(new NextTaskListener());
		lowerPanel.add(nextButton, BorderLayout.WEST);
		lowerPanel.setPreferredSize(new Dimension(600, 100));
		this.add(lowerPanel, BorderLayout.SOUTH);
		
		System.out.println("works? ");
		this.setVisible(true);
		client = new HttpClient();
		client.start();

		readInTask(client);

	
	}

	private void readInTask(HttpClient client) throws InterruptedException, ExecutionException, TimeoutException {
		//this.taskPanel.repaint(new ClearMessage());

		//Request req = client.POST("http://localhost:8080/?user=rfriedman");
		ContentResponse res = client.GET("http://localhost:8080/?user=rfriedman");
		HttpFields headers =  res.getHeaders();
		Iterator<HttpField> iter = headers.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
		System.out.println(res.getRequest().getAttributes());
		System.out.println(res.getRequest().getAttributes());
		Object m = res.getHeaders();
		Object obj = null;
		
		try {
			ObjectInputStream inStream = new ObjectInputStream(
					new ByteArrayInputStream(res.getContent()));
			obj = inStream.readObject();

			addTask(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}

	private void addPaintTask(Object obj) {
		this.taskPanel.removeAll();
		Task g = (Task) obj;
		PaintMessage h = (PaintMessage) g.getChallenge();
		String answer = g.getAnswer();
		this.lowerPanel.setAnswer(answer);
		this.taskPanel.repaint(h);
		this.repaint();
	}
	

	private void addTask(Object obj) throws IOException {
		this.taskPanel.removeAll();
		Task task = (Task) obj;
		TaskPanel p= taskPanelFactory.generatePanel(task.getChallenge(), task.getTaskType());
		this.taskPanel.add(p);
		this.lowerPanel.setAnswer(task.getAnswer());

	}
	
	private class NextTaskListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				readInTask(client);
			} catch (InterruptedException | ExecutionException
					| TimeoutException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	public static void main(String[] main) throws UnknownHostException,
			IOException, ClassNotFoundException {

		try {
			new Client();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
