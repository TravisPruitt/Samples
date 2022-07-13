<%@ Page Title="Home Page" Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true"
    CodeBehind="Default.aspx.cs" Inherits="Disney.xBand.Provisionator.UI._Default" %>

<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="ajaxToolkit" %>
<asp:Content ID="HeaderContent" runat="server" ContentPlaceHolderID="HeadContent">
</asp:Content>
<asp:Content ID="BodyContent" runat="server" ContentPlaceHolderID="MainContent">
    <script type="text/javascript">
        var ModalProgress = '<%= ModalProgress.ClientID %>';         
    </script>
    <script type="text/javascript" src="scripts/jsUpdateProgress.js"></script>
    <script type="text/javascript">
        function hidepopup() {
            var modalEditor = $find('modalEditor'); // check the behavior you defined
            modalEditor.hide();
        }
    </script>
    <asp:ScriptManager ID="ScriptManager1" runat="server" />
    <asp:Panel ID="panelUpdateProgress" runat="server" CssClass="updateProgress">
        <asp:UpdateProgress ID="UpdateProg1" DisplayAfter="0" runat="server">
            <ProgressTemplate>
                <div style="position: relative; top: 30%; text-align: center;">
                    <img src="images/loading.gif" style="vertical-align: middle" alt="Processing" />
                    Processing ...
                </div>
            </ProgressTemplate>
        </asp:UpdateProgress>
    </asp:Panel>
    <%--    <asp:Panel ID="editPanel" runat="server">
        <div style="position: relative; top: 30%; text-align: center;">
            <asp:Label ID="lblresult" Text="TEST" runat="server" />
            <asp:Button ID="editPanelCloseButton" Text="Done" runat="server" OnClick="editPanelCloseButton_OnClick" OnClientClick="javascript:hidepopup()" />
        </div>
    </asp:Panel>
    --%>
    <ajaxToolkit:ModalPopupExtender ID="ModalProgress" runat="server" TargetControlID="panelUpdateProgress"
        BackgroundCssClass="modalBackground" PopupControlID="panelUpdateProgress" />
    <%--    <ajaxToolkit:ModalPopupExtender ID="modalEditor" runat="server" TargetControlID="editPanel"
        PopupControlID="editPanel" />
    --%>
    <div style="width: 60%;">
        <asp:UpdatePanel ID="updatePanel" runat="server">
            <ContentTemplate>
                <asp:DataGrid ID="scheduledDemoDataGrid" BorderColor="black" BorderWidth="1" CellPadding="3"
                    runat="server" AutoGenerateColumns="false" OnItemCommand="scheduledDemoDataGrid_ItemCommand"
                    Width="95%">
                    <HeaderStyle HorizontalAlign="Left" />
                    <Columns>
                        <asp:ButtonColumn CommandName="Edit" Text="Edit" />
                        <asp:ButtonColumn CommandName="Setup" Text="Add" />
                        <asp:ButtonColumn CommandName="Remove" Text="Remove" />
                        <asp:BoundColumn HeaderText="ID" DataField="DemoID" Visible="false" />
                        <asp:BoundColumn HeaderText="Name" DataField="DemoDescription" />
                        <asp:BoundColumn HeaderText="Time" DataField="ScheduledTime" />
                    </Columns>
                </asp:DataGrid>
                <div>
                <asp:Button ID="addButton" Text="+" runat="server" />
                </div>
                <div>
                    <asp:Label ID="resultsLabel" runat="server" Text=""></asp:Label>
                </div>
            </ContentTemplate>
        </asp:UpdatePanel>
    </div>
</asp:Content>
