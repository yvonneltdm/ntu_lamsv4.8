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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.usermanagement;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** 
 *        @hibernate.class
 *         table="lams_auth_method_type"
 *     
*/
public class AuthenticationMethodType implements Serializable {

    /** identifier field */
    private Integer authenticationMethodTypeId;

    /** persistent field */
    private String description;

    /** persistent field */
    private Set authenticationMethods;

    /** full constructor */
    public AuthenticationMethodType(String description, Set authenticationMethods) {
        this.description = description;
        this.authenticationMethods = authenticationMethods;
    }

    /** default constructor */
    public AuthenticationMethodType() {
    }

    /** 
     *            @hibernate.id
     *             generator-class="native"
     *             type="java.lang.Integer"
     *             column="authentication_method_type_id"
     *         
     */
    public Integer getAuthenticationMethodTypeId() {
        return this.authenticationMethodTypeId;
    }

    public void setAuthenticationMethodTypeId(Integer authenticationMethodTypeId) {
        this.authenticationMethodTypeId = authenticationMethodTypeId;
    }

    /** 
     *            @hibernate.property
     *             column="description"
     *             length="64"
     *             not-null="true"
     *         
     */
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** 
     *            @hibernate.set
     *             lazy="true"
     *             inverse="true"
     *             cascade="none"
     *            @hibernate.collection-key
     *             column="authentication_method_type_id"
     *            @hibernate.collection-one-to-many
     *             class="org.lamsfoundation.lams.usermanagement.AuthenticationMethod"
     *         
     */
    public Set getAuthenticationMethods() {
        return this.authenticationMethods;
    }

    public void setAuthenticationMethods(Set authenticationMethods) {
        this.authenticationMethods = authenticationMethods;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("authenticationMethodTypeId", getAuthenticationMethodTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof AuthenticationMethodType) ) return false;
        AuthenticationMethodType castOther = (AuthenticationMethodType) other;
        return new EqualsBuilder()
            .append(this.getAuthenticationMethodTypeId(), castOther.getAuthenticationMethodTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAuthenticationMethodTypeId())
            .toHashCode();
    }

}
