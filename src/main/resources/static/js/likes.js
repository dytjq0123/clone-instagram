let page = 0;

function storyLoad() {
    $.ajax({
        url: `/api/post/likes?page=${page}`,
        dataType: "json"
    }).done(res => {
        if(res.totalElements == 0) {

        }
        res.content.forEach((post) => {
            let postItem = getLikesItem(post);
            $("#img-box").append(postItem);
        });
    }).fail(error => {
        console.log("오류", error);
    });
}

storyLoad();

$(window).scroll(() => {
    let checkNum = $(window).scrollTop() - ($(document).height() - $(window).height() );
    //console.log(checkNum);

    if(checkNum < 1 && checkNum > -1){
        page++;
        storyLoad();
    }
});

function getLikesItem(post) {
    let item = `
        <div class="img-box" onclick="postPopup(${post.id}, '.modal-post')" >                   
            <img src="/upload/${post.postImgUrl}" onerror="this.src='/img/default_profile.png';" />
                <div class="comment">
                    <a> <i class="fas fa-heart"></i><span>${post.likesCount}</span></a>
                </div>
        </div>
    `;

    return item;
}

//포스트
function postPopup(postId, obj) {
    $(obj).css("display", "flex");

    $.ajax({
        url: "/api/post/" + postId,
        dataType: "json"
    }).done(res => {
        let item = getPostModalInfo(res);
        $("#postInfoModal").append(item);
    }).fail(error => {
        console.log("post 정보 불러오기 오류", error);
    });
}

function modalClose() {
    $(".modal-post").css("display", "none");
    location.reload();
}

function getPostModalInfo(postInfoDto) {
    let item = `
    <div class="subscribe-header">
            <a href="/user/profile/${postInfoDto.postUploader.id}"><img class="post-img-profile pic" src="/profile_img/${postInfoDto.postUploader.profileImgUrl}" onerror="this.src='/img/default_profile.png'""></a>
            <span>${postInfoDto.postUploader.name}</span> `;
    item += `<button class="exit" onclick="modalClose()"><i class="fas fa-times"></i></button>`
    if(postInfoDto.uploader) {
        item += `<button class="edit" onclick="location.href='/post/update/${postInfoDto.id}'"><i class="far fa-edit"></i></button>`
    }
    item += `
    </div>
    <div class="post-box">
	    <div class="subscribe__img">
		    <img src="/upload/${postInfoDto.postImgUrl}" />
	    </div>
	    <div class="post-div">
	    <div class="post-info">
	        <div class="text"> `;
    if(postInfoDto.likeState) {
        item += `<i class="fas fa-heart active" id="storyLikeIcon" onclick="toggleLike(${postInfoDto.id})">${postInfoDto.likesCount}</i>`;
    } else {
        item += `<i class="far fa-heart" id="storyLikeIcon" onclick="toggleLike(${postInfoDto.id})">${postInfoDto.likesCount}</i>`;
    }
    item += `
            </div>
	        <div class="text">
	            <span>${postInfoDto.text}</span>
            </div>
	        <div class="tag">`;
    let arr = postInfoDto.tag.split(',');

    for(let i = 0; i < arr.length; i++) {
        item += `<span class="tag-span" onclick="location.href='/post/search?tag=${arr[i]}'">#${arr[i]} </span>`;
    }
    item += `
            </div>
        </div>
        <div class="subscribe__img">
            <span>${postInfoDto.createdate.toLocaleString()}</span>
        </div>
        <div class="comment-section" >
                <ul class="comments" id="storyCommentList-${postInfoDto.id}">`;
    postInfoDto.commentList.forEach((comment)=>{
        item += `<li id="storyCommentItem-${comment.id}">
                               <span><span class="point-span userID">${comment.user.name}</span>${comment.text}</span>`;
        if(principalId == comment.user.id) {
            item += `<button onclick="deleteComment(${comment.id})" class="delete-comment-btn">
                                                <i class="fas fa-times"></i>
                                            </button>`;
        }
        item += `</li>`});
    item += `
                </ul>
            </div>
            </div>
            <div class="comment_input">
                    <input id="storyCommentInput-${postInfoDto.id}" class="input-comment-post" type="text" placeholder="댓글 달기..." >
                    <button type="button" class="submit-comment" onClick="addComment(${postInfoDto.id})">게시</button>
            </div>
        </div>
    </div>`;
    return item;
}
function toggleLike(postId) {
    let likeIcon = $("#storyLikeIcon");

    if (likeIcon.hasClass("far")) { // 좋아요 하겠다
        $.ajax({
            type: "post",
            url: `/api/post/${postId}/likes`,
            dataType: "text"
        }).done(res=>{
            let likeCountStr = $("#storyLikeIcon").text();
            let likeCount = Number(likeCountStr) + 1;
            $("#storyLikeIcon").text(likeCount);

            likeIcon.addClass("fas");
            likeIcon.addClass("active");
            likeIcon.removeClass("far");
        }).fail(error=>{
            console.log("오류", error);
        });
    } else { // 좋아요취소 하겠다
        $.ajax({
            type: "delete",
            url: `/api/post/${postId}/likes`,
            dataType: "text"
        }).done(res=>{
            let likeCountStr = $("#storyLikeIcon").text();
            let likeCount = Number(likeCountStr) - 1;
            $("#storyLikeIcon").text(likeCount);

            likeIcon.removeClass("fas");
            likeIcon.removeClass("active");
            likeIcon.addClass("far");
        }).fail(error=>{
            console.log("오류", error);
        });
    }
}

//댓글 추가
function addComment(postId) {
    let commentInput = $(`#storyCommentInput-${postId}`);
    let commentList = $(`#storyCommentList-${postId}`);

    let data = {
        postId: postId,
        text: commentInput.val()
    }

    if (data.text === "") {
        alert("댓글을 작성해주세요!");
        return;
    }

    $.ajax({
        type: "post",
        url: "/api/comment",
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    }).done(res=>{
        console.log("성공", res);
        let comment = res;
        let content = `
		    <li id="storyCommentItem-${comment.id}">
                 <span><span class="point-span userID">${comment.user.name}</span>${comment.text}</span>
                 <button onclick="deleteComment(${comment.id})" class="delete-comment-btn">
                    <i class="fas fa-times"></i>
                 </button>
            </li>`;
        commentList.append(content);
    }).fail(error=>{
        console.log("오류", error);
        alert(error.responseText);
    });

    commentInput.val(""); // 인풋 필드를 깨끗하게 비워준다.
}

function deleteComment(commentId) {
    $.ajax({
        type: "delete",
        url: `/api/comment/${commentId}`
    }).done(res=>{
        console.log("성공", res);
        $(`#storyCommentItem-${commentId}`).remove();
    }).fail(error=>{
        console.log("오류", error);
    });
}