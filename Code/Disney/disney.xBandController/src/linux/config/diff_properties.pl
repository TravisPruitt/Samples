#!/usr/bin/perl

package xband;
use strict;

my ($file1,$file2) = @ARGV;

my %props1;
my %props2;

defined($file1) || die "Usage: diff_properties.pl <file 1> <file 2>\n";
defined($file2) || die "Usage: diff_properties.pl <file 1> <file 2>\n";

open(my $fh1, '<', $file1) || die "$file1: $!\n";
open(my $fh2, '<', $file2) || die "$file2: $!\n";

my $line;

while ($line = <$fh1>)
{
	chomp($line);
	
	if ($line =~ /#.*/)
	{
		next;
	}

	my $value;
	my ($key, @values) = split(/=/, $line);
	
	$value = join("=",@values);

	chomp($key);
	chomp($value);

	if ($key eq "") {
		next;
	}

	$props1{$key} = $value;
}

close($fh1);

while ($line = <$fh2>)
{
	chomp($line);
	
	if ($line =~ /#.*/)
	{
		next;
	}

	my $value;
	my ($key, @values) = split(/=/, $line);
	
	$value = join("=",@values);

	chomp($key);
	chomp($value);

	if ($key eq "") {
		next;
	}

	$props2{$key} = $value;
}

close($fh2);

while ( my($key, $val) = each %props1 )
{
	my $val2;
	
	$val2 = $props2{$key};

	if (!defined($val2))
	{
		printf("$key=$val is missing and needs to be added\n");
		next;
	}

	if ($val eq $val2)
	{
		next;
	}

	printf("$key=$val2 needs to be changed to $key=$val\n");
}
