$(document).ready(function () {
    getTodos();

    $("#startDate").datepicker({
        onSelect: function (date) {
            getTodosByDatepicker();
        }
    });

    $("#endDate").datepicker({
        onSelect: function (date) {
            getTodosByDatepicker();
        }
    });

    document.getElementById("today").addEventListener('click', () => {
        let start = setStartHours(new Date());
        let end = setEndHours(new Date());

        getTodosByDate(start.getTime(), end.getTime());
    });

    document.getElementById("week").addEventListener('click', () => {
        let start = setStartHours(new Date());
        let end = addDays(new Date(), 7);
        end = setEndHours(end);

        getTodosByDate(start.getTime(), end.getTime());
    });

    document.getElementById('date').addEventListener('click', (() => {
        sortByDate();
    }));

    document.getElementById("incompleteOnly").addEventListener('change', () => {
        getTodosByDatepicker();
    });

    $('#dialog').dialog({
        autoOpen: false,
        modal: true
    });

    $("#search").on('keyup', function (e) {
        if (e.key === 'Enter') {
            getTodosBySearch();
        }
    });
});

var sortAsc = true;

function sortByDate() {
    let table = document.getElementById('tasks');
    let sorted = Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
        .sort(function(a, b) {
            let dateA = parseLocaleDateString(a.cells[2].textContent);
            let dateB = parseLocaleDateString(b.cells[2].textContent);

            return dateA - dateB;
        });

    if (!sortAsc) sorted = sorted.reverse();
    sorted.forEach(tr => table.appendChild(tr));

    sortAsc = !sortAsc;
}

function parseLocaleDateString(localeDateString) {
    let dateTime = localeDateString
        .replaceAll('.', ' ')
        .replaceAll(', ', ' ')
        .replaceAll(':', ' ')
        .split(' ');

    return new Date(dateTime[2], dateTime[1] - 1, dateTime[0], dateTime[3], dateTime[4], dateTime[5]);
}

function getTodosBySearch() {
    fetch(getTodosByNameUrl())
        .then(response => response.json())
        .then(tasks => {
            updateTable(tasks);
        });
}

function getTodosByNameUrl() {
    let params = new URLSearchParams({
        q: $("#search").val()
    });

    let url = 'http://localhost:8080/todos/find?' + params;
    return url;
}

function getTodosByDatepicker() {
    let start = setStartHours($("#startDate").datepicker('getDate'));
    let end = setEndHours($("#endDate").datepicker('getDate'));

    getTodosByDate(start.getTime(), end.getTime());
}

function setEndHours(date) {
    date.setHours(23, 59, 59, 999);
    return date;
}

function setStartHours(date) {
    date.setHours(0, 0, 0, 0);
    return date;
}

function addDays(date, addedDays) {
    date.setDate(date.getDate() + addedDays);
    return date;
}

function getTodosByDate(start, end) {
    fetch(getTodosByDateUrl(start, end))
        .then(response => response.json())
        .then(tasks => {
            setDatepicker(new Date(start), new Date(end));
            updateTable(tasks);
        });
}

function getTodosByDateUrl(start, end) {
    let params = new URLSearchParams({
        from: start,
        to: end
    });

    let incompleteOnly = document.getElementById("incompleteOnly").checked;
    if (incompleteOnly) {
        params.append("status", false);
    }

    return 'http://localhost:8080/todos/date?' + params;
}

function updateTable(tasks) {
    $("#tasks").find("tr:gt(0)").remove();

    let table = document.getElementById("tasks");
    for (let task of tasks) {
        let row = table.insertRow();

        let datas = [
            task.name,
            task.shortDesc,
            new Date(task.date).toLocaleString(),
            getStatusString(task)
        ];

        for (let data of datas) {
            let cell = row.insertCell();
            let text = document.createTextNode(data);
            cell.appendChild(text);
        }

        row.addEventListener('click', () => {
            showTodoDialog(task);
        });
    }
}

function showTodoDialog(task) {
    document.getElementById('fullDesc').textContent = task.fullDesc;
    document.getElementById('dialogDate').textContent = new Date(task.date).toLocaleString();
    document.getElementById('dialogStatus').textContent = getStatusString(task);

    $('#dialog')
        .dialog({ title: task["name"] })
        .dialog("open");
}

function getStatusString(task) {
    return task.status ? 'Completed' : 'Incomplete';
}

function getTodos() {
    fetch(getTodosUrl())
        .then(response => response.json())
        .then(tasks => {
            updateDatepicker(tasks);
            updateTable(tasks);
            updateSearch(tasks);
        });
}

function updateSearch(tasks) {
    let taskNames = tasks.map(task => ({ id: task.id, label: task.name }));
    $("#search").autocomplete({
        source: taskNames,
        select: function(event, ui) {
            const info = tasks.find(e => e.id === ui.item.id);
            showTodoDialog(info);
        }
    });
}

function updateDatepicker(tasks) {
    if (tasks.length > 0) {
        let startDate = tasks[0]['date'];
        let endDate = tasks[0]['date'];

        for (let i = 0; i < tasks.length; i++) {
            if (tasks[i]['date'] > endDate) {
                endDate = tasks[i]['date'];
            }

            if (tasks[i]['date'] < startDate) {
                startDate = tasks[i]['date'];
            }
        }

        setDatepicker(new Date(startDate), new Date(endDate));
    }
}

function setDatepicker(startDate, endDate) {
    $("#endDate").datepicker("option", "minDate", startDate);
    $("#startDate").datepicker("option", "maxDate", endDate)

    $("#startDate").datepicker("setDate", startDate);
    $("#endDate").datepicker("setDate", endDate);
}

function getTodosUrl() {
    return 'http://localhost:8080/todos';
}