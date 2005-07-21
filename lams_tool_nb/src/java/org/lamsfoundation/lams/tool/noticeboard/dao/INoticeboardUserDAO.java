/*
 *Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 *
 *This program is free software; you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *USA
 *
 *http://www.gnu.org/licenses/gpl.txt
 */


/*
 * Created on Jul 1, 2005
 */
package org.lamsfoundation.lams.tool.noticeboard.dao;

import org.lamsfoundation.lams.tool.noticeboard.NoticeboardUser;
import org.lamsfoundation.lams.tool.noticeboard.NoticeboardSession;

/**
 * @author mtruong
 */
public interface INoticeboardUserDAO {
    
    public NoticeboardUser getNbUserByUID(Long uid);
    
    public NoticeboardUser getNbUserByID(Long userId);
    
    public void saveNbUser(NoticeboardUser nbUser);
    
    public void updateNbUser(NoticeboardUser nbUser);
    
    public void removeNbUser(NoticeboardUser nbUser);
    
    public void removeNbUser(Long userId);
    
    public int getNumberOfUsers(NoticeboardSession nbSession);
   
}
