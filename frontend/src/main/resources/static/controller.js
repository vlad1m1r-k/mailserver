let incomingMailList;
let sendMailList;
let deletedMailList;
let currentMail;
let pagerInc = {
    size: 10,
    page: 0,
    sortField: 'Date',
    direction: 'desc',
    string: function () {
        return '?size=' + this.size + '&page=' + this.page + '&sort=' + this.sortField + ',' + this.direction
    },
    setSort: function (name) {
        if (this.sortField === name) {
            this.direction = this.direction === 'desc' ? 'asc' : 'desc';
        } else {
            this.sortField = name;
            this.direction = 'asc';
        }
    }
};
let pagerSend = {
    size: 10,
    page: 0,
    sortField: 'Date',
    direction: 'desc',
    string: function () {
        return '?size=' + this.size + '&page=' + this.page + '&sort=' + this.sortField + ',' + this.direction
    },
    setSort: function (name) {
        if (this.sortField === name) {
            this.direction = this.direction === 'desc' ? 'asc' : 'desc';
        } else {
            this.sortField = name;
            this.direction = 'asc';
        }
    }
};
let pagerDel = {
    size: 10,
    page: 0,
    sortField: 'date',
    direction: 'desc',
    string: function () {
        return '?size=' + this.size + '&page=' + this.page + '&sort=' + this.sortField + ',' + this.direction
    },
    setSort: function (name) {
        name = name.toLowerCase();
        if (this.sortField === name) {
            this.direction = this.direction === 'desc' ? 'asc' : 'desc';
        } else {
            this.sortField = name;
            this.direction = 'asc';
        }
    }
};
$('#btn-new-mail').click(function () {
    $('#window-modal').modal('dispose');
    $('#window-modal').load('static/templates/newMail.html');
    $('#window-modal').modal({backdrop: 'static', keyboard: false});
});

$('#localIncomingTab').on('click', updateIncoming);
$(document).ready(updateIncoming);
$('#localSendTab').on('click', updateSend);
$('#localDeletedTab').on('click', updateDeleted);
$('#checkAllI').on('click', checkAll);
$('#checkAllS').on('click', checkAll);
$('#checkAllD').on('click', checkAll);
$('#btnDelInc').on('click', deleteMail);
$('#btnDelSnd').on('click', deleteMail);
$('#btnDelDel').on('click', deleteMail);
$('#btnAdminSettings').on('click', viewAdminSettings);

function viewMail(event) {
    let mailData;
    if (this.parentElement.id === 'table-incoming') {
        mailData = incomingMailList;
    }
    if (this.parentElement.id === 'table-send') {
        mailData = sendMailList;
    }
    if (this.parentElement.id === 'table-deleted') {
        mailData = deletedMailList;
    }
    let mail;
    for (const val of mailData) {
        if (val.id === event.currentTarget.id) {
            mail = val;
            break;
        }
    }
    currentMail = mail;
    $('#window-modal').modal('dispose');
    $('#window-modal').load('static/templates/viewMail.html', function () {
        $('#mail-from').val(mail.from);
        $('#mail-to').val(mail.to);
        $('#mail-subj').val(mail.subject);
        $('#mail-body').val(mail.message);
        $('#btnAnswer').on('click', mailProcess);
        $('#btnForward').on('click', mailProcess);
        for (let i = 0; i < mail.attachments.length; i++) {
            let link = $('<a></a>').text(mail.attachments[i].name);
            link.attr('href', '/attachment/' + mail.attachments[i].id);
            $('#viewMailAttach').append(link);
            $('#viewMailAttach').append(' (' + (mail.attachments[i].size / 1048576).toFixed(2) + ' Mb), ');
        }
    });
    $('#window-modal').modal('show');
}

function updateIncoming() {
    $('input:checkbox').prop('checked', false);
    $.ajax({
        url: "/rest/mail/get/incoming" + pagerInc.string(),
        success: function (data) {
            incomingMailList = data.content;
            $('#table-incoming').html("");
            for (let i = 0; i < data.content.length; i++) {
                const row = $('<tr style="cursor: pointer"></tr>');
                row.attr('id', data.content[i].id);
                row.on('click', viewMail);
                row.append($('<td></td>')
                    .append($('<input type="checkbox" name="inc_check">')
                        .attr('value', data.content[i].id).on('click', function (event) {
                            event.stopPropagation();
                })));
                row.append($('<td></td>').text(data.content[i].from));
                row.append($('<td></td>').text(data.content[i].subject));
                row.append($('<td></td>').text(data.content[i].date));
                if (data.content[i].attachments.length > 0) {
                    row.append($('<td>&#128206;</td>'));
                } else {
                    row.append($('<td></td>'));
                }
                $('#table-incoming').append(row);
            }
            drawPager(data, $('#pagerInc'), updateIncoming, pagerInc);
        }
    })
}

function updateSend() {
    $('input:checkbox').prop('checked', false);
    $.ajax({
        url: "/rest/mail/get/sent" + pagerSend.string(),
        success: function (data) {
            sendMailList = data.content;
            $('#table-send').html("");
            for (let i = 0; i < data.content.length; i++) {
                const row = $('<tr style="cursor: pointer"></tr>');
                row.attr('id', data.content[i].id);
                row.on('click', viewMail);
                row.append($('<td></td>')
                    .append($('<input type="checkbox" name="snd_check">')
                        .attr('value', data.content[i].id).on('click', function (event) {
                            event.stopPropagation();
                        })));
                row.append($('<td></td>').text(data.content[i].to));
                row.append($('<td></td>').text(data.content[i].subject));
                row.append($('<td></td>').text(data.content[i].date));
                if (data.content[i].attachments.length > 0) {
                    row.append($('<td>&#128206;</td>'));
                } else {
                    row.append($('<td></td>'));
                }
                $('#table-send').append(row);
            }
            drawPager(data, $('#pagerSend'), updateSend, pagerSend);
        }
    })
}

function updateDeleted() {
    $('input:checkbox').prop('checked', false);
    $.ajax({
        url: "/rest/mail/get/deleted" + pagerDel.string(),
        success: function (data) {
            deletedMailList = data.content;
            $('#table-deleted').html("");
            for (let i = 0; i < data.content.length; i++) {
                const row = $('<tr style="cursor: pointer"></tr>');
                row.attr('id', data.content[i].id);
                row.on('click', viewMail);
                row.append($('<td></td>')
                    .append($('<input type="checkbox" name="del_check">')
                        .attr('value', data.content[i].id).on('click', function (event) {
                            event.stopPropagation();
                        })));
                row.append($('<td></td>').text(data.content[i].from));
                row.append($('<td></td>').text(data.content[i].to));
                row.append($('<td></td>').text(data.content[i].subject));
                row.append($('<td></td>').text(data.content[i].date));
                if (data.content[i].attachments.length > 0) {
                    row.append($('<td>&#128206;</td>'));
                } else {
                    row.append($('<td></td>'));
                }
                $('#table-deleted').append(row);
            }
            drawPager(data, $('#pagerDel'), updateDeleted, pagerDel);
        }
    })
}

function checkAll() {
    const checked = this.checked;
    $('#'+this.name).find(':checkbox').each(function () {
        $(this).prop('checked', checked);
    });
}

function deleteMail() {
    let update;
    if (this.name === 'table-incoming') update = updateIncoming;
    if (this.name === 'table-send') update = updateSend;
    if (this.name === 'table-deleted') update = updateDeleted;
    $('#'+this.name).find(':checked').each(function () {
        $.ajax({
            url: '/rest/mail/delete/' + $(this).val(),
            success: function () {
                update();
            }
        })
    })
}

$('#btnRestore').on('click', function () {
    $('#table-deleted').find(':checked').each(function () {
        $.ajax({
            url: '/rest/mail/restore/' + $(this).val(),
            success: function () {
                updateDeleted();
            }
        })
    })
});

function mailProcess() {
    const button = this.id;
    $('#window-modal').modal('hide');
    $('#window-modal').modal('dispose');
    $('#window-modal').load('static/templates/newMail.html', function () {
        if (button === 'btnAnswer') {
            $('#new-mail-to').val(currentMail.from);
            $('#new-mail-subj').val("RE: " + currentMail.subject);
            let body = "\n> " + currentMail.message.replace(/\n/g, "\n> ");
            $('#new-mail-body').val(body);
        } else {
            $('#new-mail-subj').val("FW: " + currentMail.subject);
            $('#new-mail-body').val("\nOriginal message:" +
                "\nFrom: " + currentMail.from + "\nTo: " + currentMail.to + "\nSubject: " + currentMail.subject +
            "\nMessage: \n" + currentMail.message);
        }
    });
    $('#window-modal').modal({backdrop: 'static', keyboard: false});
}

$('#btnUserSettings').on('click', function () {
    $('#window-modal').modal('dispose');
    $('#window-modal').load('static/templates/userSettings.html');
    $('#window-modal').modal({backdrop: 'static', keyboard: false});
});

function setPageSize(value, updater, pager) {
    pager.size = value;
    pager.page = 0;
    updater();
}

function drawPager(data, pager, updater, pagerSettings) {
    pager.html('');
    for (let i = 0; i < data.totalPages; i++) {
        let item = $('<li class="page-item"></li>');
        if (i === data.number) {
            item.addClass('active');
        }
        let link = $('<a class="page-link"></a>').text(i + 1);
        if (i !== data.number) {
            link.attr('href', '#');
            link.on('click', {updater: updater, pager: pagerSettings}, toPage);
        }
        item.append(link);
        pager.append(item);
    }
}

function toPage(event) {
    event.data.pager.page = this.text - 1;
    event.data.updater();
}

function sort(pager, updater, name) {
    pager.setSort(name);
    updater();
}

function viewAdminSettings() {
    $('#window-modal').modal('dispose');
    $('#window-modal').load('static/templates/adminSettings.html');
    $('#window-modal').modal({backdrop: 'static', keyboard: false});
}