$(document).ready(function () { 
    getStudents();
    getAddStudentForm().addEventListener("submit", addStudentFormSubmitHandler);
    getDeleteStudentForm().addEventListener("submit", deleteStudentFormSubmitHandler);
});

function getStudents() {
    fetch(getUrl())
        .then(response => response.json())
        .then(students => {
            let studentsTable = document.getElementById('studentsTable');

            for (let student of students) {
                let row = studentsTable.insertRow();
                for (key in student) {
                    let cell = row.insertCell();
                    let text = document.createTextNode(student[key]);
                    cell.appendChild(text);
                }
            }
        });
}

function addStudentFormSubmitHandler() {
    let formData = new FormData(getAddStudentForm());

    fetch(getUrl(), {
        method: "POST",
        body: formData
    });
}

function deleteStudentFormSubmitHandler() {
    let id = getDeleteStudentForm().elements["id"];
    
    fetch(getUrl() + '/' + id.value, { method: "DELETE" });
}

function getUrl() {
    return 'http://localhost:8080/students';
}

function getAddStudentForm() {
    return document.getElementById("addStudentForm");
}

function getDeleteStudentForm() {
    return document.getElementById("deleteStudentForm");
}