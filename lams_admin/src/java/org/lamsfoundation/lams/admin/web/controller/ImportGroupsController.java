/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

package org.lamsfoundation.lams.admin.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.lamsfoundation.lams.admin.service.AdminServiceProxy;
import org.lamsfoundation.lams.admin.service.IImportService;
import org.lamsfoundation.lams.admin.web.form.ImportExcelForm;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jliew
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
@Controller
public class ImportGroupsController {

    @Autowired
    private WebApplicationContext applicationContext;

    @RequestMapping(path = "/importgroups")
    public String execute(@ModelAttribute ImportExcelForm importForm, HttpServletRequest request) throws Exception {

	if (request.getAttribute("CANCEL") != null) {
	    return "redirect:/sysadminstart.do";
	}

	IImportService importService = AdminServiceProxy.getImportService(applicationContext.getServletContext());
	importForm.setOrgId(0);
	MultipartFile file = importForm.getFile();

	// validation
	if (file == null || file.getSize() <= 0) {
	    return "import/importGroups";
	}

	String sessionId = SessionManager.getSession().getId();
	List results = importService.parseGroupSpreadsheet(file, sessionId);
	request.setAttribute("results", results);

	return "import/importGroups";
    }

}
