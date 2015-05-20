package fs.alu.ts.haprts

import scala.swing.Component
import javax.swing.JToolBar
import scala.swing.Button
import scala.swing.Action
import scala.swing.SequentialContainer
import scala.swing.event.MouseClicked

class ToolBar extends Component  with SequentialContainer.Wrapper{
override lazy val peer: JToolBar = new JToolBar
	def add( action: Action ) { 
	  peer.add( action.peer )
	}
	def add( component: Component ) { 
		
	  peer.add( component.peer )
	  
	}
	
}