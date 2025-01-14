
    function toggleDetails(rowId) {
        const detailsRow = document.getElementById('details-' + rowId);
        detailsRow.style.display = detailsRow.style.display === 'table-row' ? 'none' : 'table-row';
    }

    document.addEventListener('DOMContentLoaded', function () {
        const fileInput = document.getElementById('imagen');
        const preview = document.getElementById('preview');

        if (fileInput && preview) {
            fileInput.addEventListener('change', function (event) {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        preview.src = e.target.result;
                        preview.style.display = 'block'; // Muestra la imagen
                    };
                    reader.readAsDataURL(file);
                } else {
                    preview.style.display = 'none'; // Si no hay archivo, no muestra nada
                }
            });
        }
    });

    document.addEventListener('DOMContentLoaded', () => {
        // Selecciona todos los contenedores de vehículos
        const vehicleCards = document.querySelectorAll('.vehicle-card');

        vehicleCards.forEach(card => {
            card.addEventListener('click', () => {
                // Obtener la matrícula del atributo data-matricula
                const matricula = card.getAttribute('data-matricula');

                // Construir la URL dinámica
                const url = `http://localhost:8080/vehicles/selected/${matricula}`;

                // Redirigir a la URL
                window.location.href = url;
            });
        });
    });

    function redirigir(baseIrl) {
        // Obtener la última parte de la URL (la matrícula)
        const urlParts = window.location.pathname.split('/');
        const matricula = urlParts[urlParts.length - 1];

        // Redirigir a la página de edición con la matrícula
        window.location.href = `${baseIrl}/${matricula}`;
    }


        function toggleDropdown() {
            const dropdownOptions = document.getElementById('dropdown-options-LLC');
            dropdownOptions.style.display = 
                dropdownOptions.style.display === 'block' ? 'none' : 'block'; // Esto cambia de estado display de block a none, sino esta en block poner a block
        }


        window.addEventListener('click', function (e) {
            const dropdown = document.querySelector('.dropdown-checkbox');
            const dropdownOptions = document.getElementById('dropdown-options-LLC');

            if (!dropdown.contains(e.target)) {
                dropdownOptions.style.display = 'none';
            }
        });
        
      
    function updateModels() {
        const marca = document.getElementById("marca").value;
        const modelSelect = document.getElementById("model");
        const selectedModel = modelSelect.getAttribute("data-selected");

        // Limpiar modelos actuales
        modelSelect.innerHTML = '<option value="">Seleccione un modelo</option>';

        if (marca) {
            fetch('/vehicles/models/' + marca)
                .then(response => response.json())
                .then(models => {
                    models.forEach(model => {
                        const option = document.createElement("option");
                        option.value = model;
                        option.textContent = model;

                        // Seleccionar el modelo si coincide con el preseleccionado
                        if (model === selectedModel) {
                            option.selected = true;
                        }

                        modelSelect.appendChild(option);
                    });
                })
                .catch(error => console.error("Error al obtener los modelos:", error));
        }
    }

    // Ejecutar updateModels al cargar la página si hay una marca seleccionada
    document.addEventListener('DOMContentLoaded', () => {
        const marcaElement = document.getElementById("marca");
        const marca = marcaElement.value;
        if (marca) {
            updateModels();
        }

        // Mantener seleccionada la marca guardada si existe
        const selectedMarca = "${vehicle.marca}";
        if (selectedMarca) {
            Array.from(marcaElement.options).forEach(option => {
                if (option.value === selectedMarca) {
                    option.selected = true;
                }
            });
        }
    });

