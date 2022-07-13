<%@ Page Title="" Language="C#" MasterPageFile="~/Site.Master" AutoEventWireup="true"
    CodeBehind="EditDemo.aspx.cs" Inherits="Disney.xBand.Provisionator.UI.Reset" %>

<asp:Content ID="Content1" ContentPlaceHolderID="HeadContent" runat="server">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">
    <div style="width: 60%;">
    <asp:Label ID="description" Text="Description:" runat="server" />
    <asp:TextBox ID="descriptionTextBox" runat="server" />
        <asp:Label ID="previousDemoLabel" Text="Previous Demo:" runat="server" />
    <asp:DropDownList ID="previousDemoDropDownList" runat="server" />
  
<%--    <asp:Label ID="timeLabel" Text="Time:" runat="server" />
    <asp:DropDownList ID="timeDropDownList" runat="server" />
--%>    </div>
    <div style="width: 40%;">
        <asp:Button ID="saveButton" runat="server" Text="Save" />
    </div>
</asp:Content>
