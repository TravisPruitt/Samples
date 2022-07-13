<%@ Page Title="" Language="C#" MasterPageFile="~/Site.Master" AutoEventWireup="true"
    CodeBehind="ScheduledDemos.aspx.cs" Inherits="Disney.xBand.Provisionator.UI.ScheduledDemos" %>
   
<asp:Content ID="Content1" ContentPlaceHolderID="HeadContent" runat="server">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">
     <asp:ScriptManager ID="ScriptManager1" runat="server">
    </asp:ScriptManager>
   <div style="width: 100%;">
        <div>
            <asp:Label ID="Label1" runat="server" Text="Demo:" Style="padding-right: 20px;"></asp:Label>
            <asp:DropDownList ID="scheduledDemoDropDownList" runat="server" DataTextField="DemoDescription"
                OnSelectedIndexChanged="scheduledDemoDropDownList_SelectedIndexChanged" AutoPostBack="true" ViewStateMode="Enabled"
                DataValueField="DemoID">
            </asp:DropDownList>
        </div>
        <div style="padding-top: 20px;">
            <asp:DataGrid ID="scheduledDemoGuestDataGrid" BorderColor="black" BorderWidth="1" CellPadding="3"
                runat="server" AutoGenerateColumns="false" OnItemCommand="scheduledDemoGuestDataGrid_ItemCommand">
                <Columns>
                    <asp:ButtonColumn CommandName="Edit" Text="Edit" />
                    <asp:ButtonColumn CommandName="Move" Text="Move" />
                    <asp:ButtonColumn CommandName="Remove" Text="Remove" />
                    <asp:BoundColumn HeaderText="ID" DataField="GuestID" Visible="false" />
                    <asp:BoundColumn HeaderText="Last Name" DataField="LastName" DataFormatString="{0:d}" />
                    <asp:BoundColumn HeaderText="First Name" DataField="FirstName" />
                    <asp:BoundColumn HeaderText="Band ID" DataField="BandID" />
                </Columns>
            </asp:DataGrid>
<%--            <ajaxToolkit:ModalPopupExtender ID="guestDetailsPopup"
                runat="server" TargetControlID="scheduledDemoGuestDataGrid" PopupControlID="pnlPopup"   >
            </ajaxToolkit:ModalPopupExtender>
            <asp:Panel ID="pnlPopup" runat="server" Width="500px" Style="display: none">
                <asp:UpdatePanel ID="updPnlCustomerDetail" runat="server" UpdateMode="Conditional">
                    <ContentTemplate>
                        <asp:Label ID="lblCustomerDetail" runat="server" Text="Customer Detail" BackColor="lightblue"
                            Width="95%" />
                        <asp:DetailsView ID="dvCustomerDetail" runat="server" DefaultMode="Edit" Width="95%"
                            BackColor="white" />
                    </ContentTemplate>
                </asp:UpdatePanel>
                <div style="width: 95%">
                    <asp:Button ID="btnSave" runat="server" Text="Save" OnClientClick="alert('Sorry, but I didnt implement save '); return false;"
                        Width="50px" />
                    <asp:Button ID="btnClose" runat="server" Text="Close" Width="50px" />
                </div>
            </asp:Panel>
--%>        </div>
    </div>
</asp:Content>
