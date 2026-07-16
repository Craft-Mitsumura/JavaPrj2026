document.addEventListener('DOMContentLoaded', function() {
	
	/* ===========================
	   Category Button Handler (Event Delegation)
	=========================== */
	document.addEventListener('click', function(e) {
		const categoryBtn = e.target.closest('#categoryBtns');
		if (categoryBtn) {
			e.stopPropagation();
			const categoryLists = document.getElementById('categoryLists');
			if (categoryLists) {
				categoryLists.classList.toggle('show');
			}
		}
	});

	/* ===========================
	   Close Category on Outside Click
	=========================== */
	document.addEventListener('click', function(e) {
		const categoryLists = document.getElementById('categoryLists');
		const categoryBtn = document.getElementById('categoryBtns');
		
		if (categoryLists && categoryBtn) {
			if (!categoryLists.contains(e.target) && !categoryBtn.contains(e.target)) {
				categoryLists.classList.remove('show');
			}
		}
	});

	/* ===========================
	   Favorite Button Handler (Event Delegation)
	   Works for both detail.html (single button) and favorite/list.html (multiple buttons)
	=========================== */
	document.addEventListener('click', function(e) {
		// Handle both ID selector (detail.html) and class selector (favorite/list.html)
		const favoriteBtn = e.target.closest('#favoriteButton') || 
		                    e.target.closest('.favoriteButton');
		
		if (favoriteBtn) {
			e.preventDefault();
			e.stopPropagation();
			
			// Find the hidden input with itemId - handles both cases
			let itemIdInput = favoriteBtn.querySelector('#favoriteItemId') ||  // detail.html
			                  favoriteBtn.querySelector('.favoriteItemId') ||   // list.html
			                  favoriteBtn.closest('td').querySelector('.favoriteItemId'); // list.html fallback

			if (!itemIdInput) {
				console.error('Item ID input not found');
				return;
			}
			
			const itemId = itemIdInput.value;

			fetch("/shared_shop/client/favorite/add", {
				method: "POST",
				headers: {
					"Content-Type": "application/x-www-form-urlencoded"
				},
				body: "itemId=" + itemId
			})
				.then(response => response.text())
				.then(result => {
					const favoriteText = favoriteBtn.querySelector('span');

					if (result === "added") {
						if (favoriteText) {
							favoriteText.textContent = "♥ お気に入り済み";
						}
						location.reload();

					} else if (result === "detached") {
						if (favoriteText) {
							favoriteText.textContent = "♡ お気に入り";
						}
						// For list.html, remove the row
						const row = favoriteBtn.closest('tr');
						if (row) {
							row.remove();
							// Reload if no more items
							if (document.querySelectorAll(".list_table tbody tr").length === 0) {
								location.reload();
							}
						} else {
							location.reload();
						}

					} else if (result === "ng") {
						alert("ログインしてください");
					}
				})
				.catch(error => {
					console.error('Fetch error:', error);
				});
		}
	});

});