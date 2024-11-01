<%@ include file="/taglibs.jsp"%>

		<h4><fmt:message key="sysadmin.lesson.default" /></h4>
				<div class="form-check mb-2">
					<form:checkbox id="gradebookOnComplete" path="gradebookOnComplete" cssClass="form-check-input"/>
					<label class="form-check-label" for="gradebookOnComplete">
						<fmt:message key="sysadmin.lesson.gradebook.complete" />
		    </label>
		</div>
					
				<div class="form-check mb-2">
					<form:checkbox path="forceLearnerRestart" id="forceLearnerRestart" cssClass="form-check-input"/>
					<label class="form-check-label" for="forceLearnerRestart">
						<fmt:message key="sysadmin.lesson.force.restart" />
		    </label>
		</div>

				<div class="form-check mb-2">
					<form:checkbox id="allowLearnerRestart" path="allowLearnerRestart" cssClass="form-check-input"/>
					<label class="form-check-label" for="allowLearnerRestart">
						<fmt:message key="sysadmin.lesson.allow.restart" />
		    </label>
		</div>

				<div class="form-check mb-2">
					<form:checkbox id="startInMonitor" path="startInMonitor" cssClass="form-check-input"/>
					<label class="form-check-label" for="startInMonitor">
						<fmt:message key="sysadmin.lesson.start.in.monitor" />
		    </label>
		</div>

				<div class="form-check mb-2">
					<form:checkbox id="liveEditEnabled" path="liveEditEnabled" cssClass="form-check-input"/>
					<label class="form-check-label" for="liveEditEnabled">
						<fmt:message key="sysadmin.lesson.liveedit" />
		    </label>
		</div>

				<div class="form-check mb-2">
					<form:checkbox id="enableLessonNotifications" path="enableLessonNotifications" cssClass="form-check-input"/>
					<label class="form-check-label" for="enableLessonNotifications">
						<fmt:message key="sysadmin.lesson.notification" />
		    </label>
		</div>		
	</div>