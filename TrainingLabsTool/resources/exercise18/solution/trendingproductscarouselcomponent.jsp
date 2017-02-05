<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>

<c:choose>
	<c:when test="${not empty bookData}">
		<div class="carousel-component">
			<div class="headline">${title}</div>
			<div class="carousel js-owl-carousel js-owl-default">
				<c:forEach items="${bookData}" var="book">

					<c:url value="${book.url}" var="bookUrl" />

					<div class="item">
						<a href="${bookUrl}">
							<div class="thumb">
								<product:productPrimaryImage product="${book}" format="product" />
							</div>
							<div class="item-name">${book.name}</div>
							<div class="item-price">
								<format:fromPrice priceData="${book.price}" />
							</div>
						</a>
					</div>
				</c:forEach>
			</div>
		</div>
	</c:when>

	<c:otherwise>
		<component:emptyComponent />
	</c:otherwise>
</c:choose>

