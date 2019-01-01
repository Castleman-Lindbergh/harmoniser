/**
 * Copyright (c) 2009 Severin Smith
 *
 * This file is part of a library called The MidiBus (themidibus) - http://www.smallbutdigital.com/themidibus.php.
 *
 * The MidiBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The MidiBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MidiBus. If not, see <http://www.gnu.org/licenses/>.
*/

public class NoteHandler {	
	
	// construct new NoteHandler
	public NoteHandler() {
		
	}
	
	/**
	 * Is passed the channel, pitch and velocity associated with every new NoteOn MIDI message recieved by a MidiBus attached to this applet.
	 *
	 * @param channel the channel on which the NoteOn arrived
	 * @param pitch the pitch associated with the NoteOn
	 * @param velocity the velocity associated with the NoteOn
	 * @see #noteOn(int channel, int pitch, int velocity, long timestamp, String bus_name)
	*/
	public void noteOn(int channel, int pitch, int velocity) {
		System.out.println("ON: " + pitch + ", " + velocity);
	}
	
	/**
	 * Is passed the channel, pitch and velocity associated with every new NoteOff MIDI message recieved by a MidiBus attached to this applet.
	 *
	 * @param channel the channel on which the NoteOff arrived
	 * @param pitch the pitch associated with the NoteOff
	 * @param velocity the velocity associated with the NoteOff
	 * @see #noteOff(int channel, int pitch, int velocity, long timestamp, String bus_name)
	*/
	public void noteOff(int channel, int pitch, int velocity) {
		System.out.println("OFF: " + pitch + ", " + velocity);
	}
}