<%@ include file="/common/taglibs.jsp"%>
<%-- TYPE parameter can have one of the following values: correct, incorrect, letter, full.--%>

<svg id="svg-${param.svgId}" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 2718.1306 1812.087"
		${param.type == 'correct' || param.type == 'incorrect' ? ' class="scratched"' : ''}
		${param.type == 'full' && param.isHidden ? ' style="visibility: hidden;"' : ''}>
	<title>
		<c:choose>
			<c:when test="${param.type == 'correct'}">
				<fmt:message key='label.correct.answer'/>
			</c:when>
			<c:when test="${param.type == 'incorrect'}">
				<fmt:message key='label.incorrect.answer'/>
			</c:when>
			<c:otherwise}">
				<fmt:message key='label.monitoring.summary.answer'/> &#${param.letter + 65};
			</c:otherwise>
		</c:choose>
	</title>
		
	<c:if test="${param.type != 'letter'}">
		<defs>
			<mask id="icon-mask-${param.svgId}" x="0" y="0" width="2718.1306" height="1812.087" maskUnits="userSpaceOnUse">
				<path class="icon-mask" d="M461.91,647.67C685.7063,398.1926,1094.6617,94.3136,1190.05,156.683c132.0765,113.7325-900.5732,808.7991-764.828,971.5987,106.3949,121.07,1062-1092.6688,1247.3886-964.8917C1863.3878,354.1671,524.28,1179.6448,711.3878,1396.1034,825.12,1524.511,1865.1076,208.0461,2062.42,411.4919c154.0891,148.586-1156.586,962.5987-960.3058,1195.5669C1232.356,1749.087,2117.4515,660.511,2268.7891,787.0843c203.6178,154.0891-773.1975,726.42-657.6306,837.7437,156.8408,106.0525,748.4331-452.5208,748.4331-452.5208" fill="none" stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="250"/>
			</mask>
			
			<c:if test="${param.type == 'full'}">
				<filter id="luminosity-invert-noclip-${param.svgId}" x="287.8915" y="23.4364" width="2196.7002" height="1739.7852" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
					<feColorMatrix values="-1 0 0 0 1 0 -1 0 0 1 0 0 -1 0 1 0 0 0 1 0" result="invert"/>
					<feFlood flood-color="#fff" result="bg"/>
					<feBlend in="invert" in2="bg"/>
				</filter>
				<mask id="letter-mask-${param.svgId}" x="287.8915" y="23.4364" width="2196.7002" height="1739.7852" maskUnits="userSpaceOnUse">
					<path class="letter-mask" d="M461.91,647.67C685.7063,398.1926,1094.6617,94.3136,1190.05,156.683c132.0765,113.7325-900.5732,808.7991-764.828,971.5987,106.3949,121.07,1062-1092.6688,1247.3886-964.8917C1863.3878,354.1671,524.28,1179.6448,711.3878,1396.1034,825.12,1524.511,1865.1076,208.0461,2062.42,411.4919c154.0891,148.586-1156.586,962.5987-960.3058,1195.5669C1232.356,1749.087,2117.4515,660.511,2268.7891,787.0843c203.6178,154.0891-773.1975,726.42-657.6306,837.7437,156.8408,106.0525,748.4331-452.5208,748.4331-452.5208" fill="none" stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="250" filter="url(#luminosity-invert-noclip-${param.svgId})"/>
				</mask>
			</c:if>
		</defs>
	</c:if>
	
	<rect width="2718.1306" height="1812.087" fill="#3e3e3e"/>
	
	<c:if test="${param.type != 'letter'}">
		<g mask="url(#icon-mask-${param.svgId})" aria-live="polite">
			<g>
				<rect width="2718.1306" height="1812.087" fill="#fff"/>
				
				<c:if test="${param.type == 'correct' || param.type == 'full'}">
					<path class="tick-icon" d="M1260.2087,1072.0439h-1.3334c-9.0406-18.2514-22.228-35.3787-33.0506-52.6667-22.806-36.43-45.9227-72.7033-68.4087-109.3333-8.6426-14.078-17.56-27.9793-26.292-42-10.518-16.888-20.0006-31.8973-36.9413-42.79-16.744-10.766-39.0773-7.4494-56.6407-.826-37.349,14.0853-80.9138,47.8906-66.6476,92.9493,4.0245,12.7114,12.04,23.502,19.0314,34.6667,11.9189,19.0353,24.3256,37.7833,36.4882,56.6667,36.4567,56.6013,73.0321,113.1453,109.0927,170,13.286,20.9466,26.4993,42.0267,40.2573,62.6667,9.2454,13.8666,16.664,27.02,31.1114,36.2,29.5313,18.78,80.5633,11.2267,103.938-14.2,9.2493-10.06,14.6893-24.6333,20.9006-36.6667,11.514-22.3067,23.4714-44.5673,34.034-67.3333,6.1907-13.3407,14.0307-26.144,20.6254-39.3333,13.3417-26.7342,28.19-54.8761,41.3746-79.3334,14.6867-27.156,29.068-54.5146,44.376-81.3333,37.0253-64.8666,74.9673-128.8186,115.8553-191.3333,7.26-12.7592,12.4136-20.2318,18.1227-26.6793,16.32-21.0567,30.9267-43.3447,47.8467-63.9874a610.5662,610.5662,0,0,1,56.26-60.6073c10.5734-9.8353,24.7334-17.388,33.6533-28.726,8.5133-10.82,7.9267-25.7925-.16-36.6667-15.0668-20.2581-43.5668-27.3334-67.4934-27.3334-53.8934,0-103.5587,29.5252-141.3334,66.6687-39.5819,38.9213-71.1366,85.434-101.1279,131.998-50.0067,77.6387-92.7121,159.8127-130.5294,244C1287.666,1001.3312,1274.766,1037.0326,1260.2087,1072.0439Z" fill="#7cb643"/>
				</c:if>
				
				<c:if test="${param.type == 'incorrect' || param.type == 'full'}">
					<path class="cross-icon" d="M1328.6179,1024.4649c4.7293,2.5633,7.9087,7.906,11.2773,12,7.7114,9.3714,15.0834,19.0534,22.49,28.6667,26.4027,34.2667,50.186,71.062,77.4634,104.5534,22.2286,27.2933,51.252,40.78,86.1026,40.78,17.1853,0,35.4934-3.9867,45.536-19.3333,4.5633-6.9734,7.4186-15.26,9.3-23.3333a79.8044,79.8044,0,0,0,1.64-26.6667c-4.4247-36.9953-22.2154-67.4994-41.5267-98.6667-19.1126-30.848-39.046-61.1607-60.1893-90.6666-12.024-16.7814-23.8813-33.7474-36.62-50-3.612-4.608-13.96-14.1247-14.3327-20-.296-4.6734,7.8114-12.3107,10.4754-16,10.5446-14.604,21.6573-28.2587,32.4686-42.6667,45.0474-60.0327,94.974-118.436,152.582-167.15a298.5746,298.5746,0,0,1,30.6667-22.9407c8.0933-5.2193,16.7466-9.1927,23.2066-16.576,10.9734-12.5227,8.2867-29.146-1.62-41.3333a62.7456,62.7456,0,0,0-13.5866-12.2194c-56.3454-38.0483-129.3354-8.7633-179.3334,25.536-9.6226,6.6014-19.0553,13.468-28,20.9754-10.9673,9.2053-20.7773,19.376-30.6406,29.708-12.4333,13.0246-24.2173,26.8273-35.736,40.6666-16.1573,19.4134-31.3627,39.4794-46.9567,59.3334-3.7846,4.8186-7.402,9.7693-11.0846,14.6666-1.182,1.5727-2.778,4.5827-4.932,4.9154-2.9314.452-6.7987-5.6687-8.5294-7.5227-7.644-8.1887-14.8779-16.7913-22.7873-24.726-18.1673-18.226-35.6533-37.1833-53.7173-55.524-13.9534-14.1673-27.0374-28.96-43.616-40.2467-9.4027-6.4013-19.2807-12.2626-30-16.194-36.3827-13.342-72.676-3.7773-103.3334,18.3487-20.7706,14.99-37.9838,37.266-32.324,64.2827,5.5606,26.5433,27.1987,45.482,46.324,62.78,26.8967,24.3266,53.0254,49.5773,78.6667,75.22,17.976,17.9773,36.086,35.8273,53.464,54.384,3.5853,3.828,16.8767,13.786,15.1673,19.616-2.54,8.662-10.91,17.6733-15.6813,25.3333-11.8153,18.9693-23.492,38.092-34.8533,57.3334q-29.349,49.7038-57.6634,100c-5.9413,10.596-12.028,21.16-17.5513,32-2.012,3.95-2.6133,8.2139-5.328,11.8866-5.5707,7.536-9.168,15.74-13.4807,24-12.5566,24.0466-36.7573,57.02-18.054,84.1134,6.74,9.76,18.1554,15.5666,29.314,18.76,25.752,7.3733,61.7134,6.8133,84-9.7467,10.5267-7.82,17.1174-20.1133,24.0354-31.0133,10.5206-16.58,21.2339-32.9867,31.184-49.88,10.0873-17.12,21.9793-33.486,32.7446-50.1227,5.958-9.2073,10.32-19.0933,16.822-27.9713,11.1087-15.1667,21.386-30.888,31.6774-46.6926,4.7767-7.3354,11.324-14.698,14.87-22.6667Z" fill="#c7292b"/>
				</c:if>
			</g>
		</g>
	</c:if>
	
	<c:if test="${param.type == 'letter' || param.type == 'full'}">
		<g mask="url(#letter-mask-${param.svgId})" aria-live="polite">
			<text transform="translate(900 1350)" font-size="1300" fill="#fff" font-family="Roboto" font-weight="700" letter-spacing="-0.0371em">
				&#${param.letter + 65};
			</text>
		</g>
	</c:if>
</svg>
